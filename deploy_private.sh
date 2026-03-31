#!/bin/bash

# --- Private 서버 정보 ---
PRIVATE_HOST="10.1.2.6"
PRIVATE_USER="root"
PRIVATE_KEY="/root/.ssh/id_rsa"

# --- 퍼블릭 서버에서 남길 배포 로그 파일 ---
exec > /root/app/deploy_private.log 2>&1
echo "Starting deployment to Private server..."

# SSH agent 실행
eval "$(ssh-agent -s)"

# 퍼블릭 서버에 저장된 Private 서버 접속용 개인키 등록
ssh-add "$PRIVATE_KEY"

# --- Private(WAS) 서버에 프로젝트 폴더 생성 ---
ssh -o StrictHostKeyChecking=no "$PRIVATE_USER@$PRIVATE_HOST" "mkdir -p /root/app"

# --- 퍼블릭 서버 -> Private(WAS) 서버로 빌드된 jar 복사 ---
scp -o StrictHostKeyChecking=no /root/app/target/*SNAPSHOT.jar "$PRIVATE_USER@$PRIVATE_HOST:/root/app/app.jar"

# --- Private(WAS) 서버에서 Spring Boot 서비스 재시작 ---
# 기존 nohup java -jar 방식 대신 systemd 서비스(recyq-web) 사용
ssh -o StrictHostKeyChecking=no "$PRIVATE_USER@$PRIVATE_HOST" "
systemctl daemon-reload && \
systemctl restart recyq-web
"

# --- 보안상 ssh-agent 종료 ---
ssh-agent -k

echo 'Deployment to Private server completed.'