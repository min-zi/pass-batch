package com.ming.pass.job.statistics;

import com.ming.pass.repository.statistics.AggregatedStatistics;
import com.ming.pass.repository.statistics.StatisticsRepository;
import com.ming.pass.util.CustomCSVWriter;
import com.ming.pass.util.LocalDateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 통계 데이터를 주 단위로 집계해서 CSV 파일로 저장

@Slf4j
@Component
@StepScope
public class MakeWeeklyStatisticsTasklet implements Tasklet {
    @Value("#{jobParameters[from]}")
    private String fromString;
    @Value("#{jobParameters[to]}")
    private String toString;

    private final StatisticsRepository statisticsRepository;

    public MakeWeeklyStatisticsTasklet(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
        final LocalDateTime to = LocalDateTimeUtils.parse(toString);

        // from~to 범위 내의 데이터를 날짜별로 group by 해서 가져옴 (하루 단위로 allCount, attendedCount, cancelledCount 같은 통계들이 들어 있음)
        final List<AggregatedStatistics> statisticsList = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to);

        Map<Integer, AggregatedStatistics> weeklyStatisticsEntityMap = new LinkedHashMap<>();

        for (AggregatedStatistics statistics : statisticsList) {
            int week = LocalDateTimeUtils.getWeekOfYear(statistics.getStatisticsAt()); // 각 일별 통계 날짜를 기준으로 몇 번째 주인지 계산해서
            AggregatedStatistics savedStatisticsEntity = weeklyStatisticsEntityMap.get(week); // 해당 주의 통계가 Map 에 있는지 확인

            // 없다면 새로 추가, 이미 있다면 병합
            if (savedStatisticsEntity == null) {
                weeklyStatisticsEntityMap.put(week, statistics);

            } else {
                savedStatisticsEntity.merge(statistics);

            }

        }

        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"week", "allCount", "attendedCount", "cancelledCount"});
        weeklyStatisticsEntityMap.forEach((week, statistics) -> {
            data.add(new String[]{
                    "Week " + week,
                    String.valueOf(statistics.getAllCount()),
                    String.valueOf(statistics.getAttendedCount()),
                    String.valueOf(statistics.getCancelledCount())
            });

        });
        CustomCSVWriter.write("weekly_statistics_" + LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD) + ".csv", data);
        return RepeatStatus.FINISHED;

    }

}
