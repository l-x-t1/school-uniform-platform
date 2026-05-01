#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20240629.sql ./mysql/db
cp ../sql/ry_config_20240902.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../xfglpt-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy xfglpt-gateway "
cp ../xfglpt-gateway/target/xfglpt-gateway.jar ./ruoyi/gateway/jar

echo "begin copy xfglpt-auth "
cp ../xfglpt-auth/target/xfglpt-auth.jar ./ruoyi/auth/jar

echo "begin copy xfglpt-visual "
cp ../xfglpt-visual/xfglpt-monitor/target/xfglpt-visual-monitor.jar  ./ruoyi/visual/monitor/jar

echo "begin copy xfglpt-modules-system "
cp ../xfglpt-modules/xfglpt-system/target/xfglpt-modules-system.jar ./ruoyi/modules/system/jar

echo "begin copy xfglpt-modules-file "
cp ../xfglpt-modules/xfglpt-file/target/xfglpt-modules-file.jar ./ruoyi/modules/file/jar

echo "begin copy xfglpt-modules-job "
cp ../xfglpt-modules/xfglpt-job/target/xfglpt-modules-job.jar ./ruoyi/modules/job/jar

echo "begin copy xfglpt-modules-gen "
cp ../xfglpt-modules/xfglpt-gen/target/xfglpt-modules-gen.jar ./ruoyi/modules/gen/jar

