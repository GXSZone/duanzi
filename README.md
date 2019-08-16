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

后期功能:H5链接打开APP指定页面,linkMe
[参考文章](https://www.jianshu.com/p/9baff4d50951)


