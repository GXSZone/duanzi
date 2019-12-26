# duanzi
##小视频项目,可直接运行,采用最新的androidX 版本,和最新版本的studio,最高支持28
目前采用的有ijk 和 exo 两个内核解码器,android 原生的太辣鸡了,播放贼慢
后期重新整改glideimage库 ,把rwigitHelper的功能添加进去,库本身的圆角和圆形的图片通过glide加载有些问题,通过裁剪画布的方式比较靠谱

| 技术点 |
| :------------ |
|  1.全局采用context 的地方都用 application 记录的activity,防止传参导致的内存泄露 |
|  2.利用 activity-alias 抽离恶心的微信回调问题 |
|  3.优化 Ijkplayer ,关键都在 setOptions 设置里,包括http和https 视频链接切换播放问题 |
|  4.视频处理用的是蓝松SDK,加片头片尾已经视频加水印,视频转码  |
|  5.在内存紧张的时候释放glide的内存缓存(在application 回调里)  |
|  6.通过给view设置tag的方式,方便统一处理埋点的问题(未登录和登录都搞定) |
|  7.正式版设置 okhttp 来防止抓包功能  |

骨架loading效果:https://www.jianshu.com/p/ded3c7e8adfe

##后期改进可选框架:
###[比sp和mmkv更优的key_value框架](https://github.com/JeremyLiao/FastSharedPreferences)
###[比EventBus更简单,包更小的事件框架](https://github.com/JeremyLiao/LiveEventBus)
###[详情左右滑动改进](https://github.com/YvesCheung/SlidableLayout)
### fragment 框架引入,状态栏工具类抽取使用


