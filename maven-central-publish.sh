#!/bin/sh
#CI/CD持续性发布
prepare_publish_components="mvn clean test"
#以命令行的方式使用（-Dgpg.signer=bc）GPG对组件进行签名
publishing_components="mvn deploy -Dgpg.signer=bc -Dgpg.keyFilePath='$GPG_KEY_FILE_PATH' -pl suda-core,suda-spring-boot-autoconfigure,suda-spring-boot-starter"
eval "$prepare_publish_components"
if [ $? -eq 0 ]; then
  eval "$publishing_components"
else
   echo "项目清理和测试失败，错误码 $?, 按任意键退出" && read -n 1 -s && exit 0
fi
if [ $? -eq 0 ]; then
  echo "项目发布成功, 按任意键退出"
else
  echo "项目发布失败，错误码 $?, 按任意键退出"
fi
read -n 1 -s
exit 0