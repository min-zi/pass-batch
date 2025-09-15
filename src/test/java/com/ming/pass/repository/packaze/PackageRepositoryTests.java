package com.ming.pass.repository.packaze;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PackageRepositoryTests {

    @Autowired
    private PackageRepository packageRepository;

    @Test
    public void package_save() {
        // null 상태에서 insert 로 auto_increment 값이 세팅되는지 확인
        //given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디 챌린지 PT 12주");
        packageEntity.setPeriod(84);

        //when
        packageRepository.save(packageEntity);

        //then
        assertNotNull(packageEntity.getPackageSeq());
    }

    @Test
    public void package_findByCreatedAtAfter() {
        // 여러 데이터 중 정렬/페이징이 잘 동작해서 가장 마지막에 저장된 패키지가 조회되는지 확인
        //given
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1); // 기준 시간을 1분 전으로 설정해서 방금 저장한 엔티티들이 조회되도록

        PackageEntity packageEntity0 = new PackageEntity();
        packageEntity0.setPackageName("학생 전용 3개월");
        packageEntity0.setPeriod(90);
        packageRepository.save(packageEntity0);

        PackageEntity packageEntity1 = new PackageEntity();
        packageEntity1.setPackageName("학생 전용 6개월");
        packageEntity1.setPeriod(180);
        packageRepository.save(packageEntity1);

        //when
        final List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0, 1, Sort.by("packageSeq").descending()));

        //then
        assertEquals(1, packageEntities.size());
        assertEquals(packageEntity1.getPackageSeq(), packageEntities.get(0).getPackageSeq());
    }

    @Test
    public void package_updateCountAndPeriod() {
        // 반환값으로 1건 수정 되었는지 row 수 확인, 엔티티의 count, period 의 초기값이 요청한 값으로 DB에서 갱신되는지 확인
        //given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디프로필 이벤트 4개월");
        packageEntity.setPeriod(90);
        packageRepository.save(packageEntity);

        //when
        int updatedCount = packageRepository.updateCountAndPeriod(packageEntity.getPackageSeq(), 30, 120);
        final PackageEntity updatePackageEntity = packageRepository.findById(packageEntity.getPackageSeq()).get();

        //then
        assertEquals(1, updatedCount);
        assertEquals(30, updatePackageEntity.getCount());
        assertEquals(120, updatePackageEntity.getPeriod());
    }

    @Test
    public void package_delete() {
        // 엔티티를 삭제하면 더 이상 DB에서 조회되지 않는지 확인
        //given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("제거할 이용권");
        packageEntity.setCount(1);
        PackageEntity newPackageEntity = packageRepository.save(packageEntity);

        //when
        packageRepository.deleteById(newPackageEntity.getPackageSeq());

        //then
        assertTrue(packageRepository.findById(newPackageEntity.getPackageSeq()).isEmpty());

    }
}