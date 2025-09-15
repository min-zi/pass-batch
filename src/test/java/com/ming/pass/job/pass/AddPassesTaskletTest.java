package com.ming.pass.job.pass;

import com.ming.pass.repository.pass.*;
import com.ming.pass.repository.user.UserGroupMappingEntity;
import com.ming.pass.repository.user.UserGroupMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.time.LocalDateTime;
import java.util.List;

import org.mockito.ArgumentMatchers.*;
import org.springframework.batch.repeat.RepeatStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class) // @Mock, @InjectMocks 을 사용하기 위한 JUnit5
public class AddPassesTaskletTest {
    @Mock
    private StepContribution stepContribution;

    @Mock
    private ChunkContext chunkContext;

    @Mock
    private PassRepository passRepository;

    @Mock
    private BulkPassRepository bulkPassRepository;

    @Mock
    private UserGroupMappingRepository userGroupMappingRepository;

    @InjectMocks // @InjectMocks 클래스의 인스턴스를 생성하고 @Mock 으로 생성된 객체를 주입
    private AddPassesTasklet addPassesTasklet;

    @Test
    public void execute() {
        //given
        final String userGroupId = "GROUP";
        final String userId = "A1000000";
        final Integer packageSeq = 1;
        final Integer count = 10;

        final LocalDateTime now = LocalDateTime.now();

        final BulkPassEntity bulkPassEntity = new BulkPassEntity();
        bulkPassEntity.setPackageSeq(packageSeq);
        bulkPassEntity.setUserGroupId(userGroupId);
        bulkPassEntity.setStatus(BulkPassStatus.READY);
        bulkPassEntity.setCount(count);
        bulkPassEntity.setStartedAt(now);
        bulkPassEntity.setEndedAt(now.plusDays(60));

        final UserGroupMappingEntity userGroupMappingEntity = new UserGroupMappingEntity();
        userGroupMappingEntity.setUserGroupId(userGroupId);
        userGroupMappingEntity.setUserId(userId);

        //when
        when(bulkPassRepository.findByStatusAndStartedAtGreaterThan(eq(BulkPassStatus.READY),any())).thenReturn(List.of(bulkPassEntity));
        when(userGroupMappingRepository.findByUserGroupId(eq("GROUP"))).thenReturn(List.of(userGroupMappingEntity));

        RepeatStatus repeatStatus = addPassesTasklet.execute(stepContribution, chunkContext);

        //then
        // execute 의 return 값인 RepeatStatus 값을 확인
        assertEquals(RepeatStatus.FINISHED, repeatStatus);

        // passRepository.saveAll 이 한번 실행됐는지 검증, 호출될 때 전달된 인자(리스트)를 캡처 객체에 담아서 검사
        ArgumentCaptor<List> passEntitiesCaptor = ArgumentCaptor.forClass(List.class);
        verify(passRepository, times(1)).saveAll(passEntitiesCaptor.capture());
        final List<PassEntity> passEntities = passEntitiesCaptor.getValue();

        assertEquals(1, passEntities.size());
        final PassEntity passEntity = passEntities.get(0);
        assertEquals(packageSeq, passEntity.getPackageSeq());
        assertEquals(userId, passEntity.getUserId());
        assertEquals(PassStatus.READY, passEntity.getStatus());
        assertEquals(count, passEntity.getRemainingCount());

    }
}
