# 백그라운드 실행, 강제 재생성
# 업은 컨테이너를 생성하고 실행
# 옵션 d 는 백그라운드 실행
# force-recreate 는 반드시 컨테이너를 지우고 새로 만듬(docker compose 파일을 수정했다면 이 옵션을 줘야 됨)
db-up:
	docker-compose up -d --force-recreate

# volume 삭제
# 다운은 컨테이너를 정지하고 삭제
# 옵션 v 는 볼륨까지 삭제
db-down:
	docker-compose down -v