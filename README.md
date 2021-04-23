# V2EX 交易监控机器人
这是一个 Telegram Bot，用来监控 V2EX 交易区的交易，当监控有人发帖的主题或内容匹配关键词时，会发送消息提醒用户。

这里已经有一个在运行的 Demo：[v2ex_spider_bot](https://t.me/v2ex_spider_bot)，可以直接关注使用。



## 支持指令

1. /start - 向用户发送 Hello。
2. /addkeyword - 添加一个关键词
3. /listkeyword - 列出已添加的关键词
4. /clearkeyword - 清除已添加的关键词
5. /latestentry - 查看最新一篇交易贴的标题和链接。



## 使用步骤

1. 在 Telegram 中，对话 [BotFather](https://t.me/botfather)，创建属于你的机器人，参考[此文](https://core.telegram.org/bots#6-botfather)。
2. 创建好机器人后，拿到机器人用户名和 Token。
3. 将上一步得到的用户名和 Token 填入到 Config.java 中。

4. 运行 Main.main 函数即可启动机器人。