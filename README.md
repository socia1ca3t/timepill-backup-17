<h1>胶囊日记一键备份项目</h1>

本项目基于胶囊官方提供的开放接口，实现了胶囊日记一键备份下载功能。备份文件的格式为HTML离线文档。
<br/>
下载程序，双击运行，即可在自己的电脑上安全地备份。

![example](https://github.com/socia1ca3t/timepill-backup/assets/147909308/a681bc57-4331-4573-97e8-d6437d2cb82a)

<h3>功能介绍</h3>
<ul>
    <li>实时提示下载进度。</li>
    <li>一键截图单本日记。</li>
    <li>一键下载单本日记或所有日记。</li>
</ul>

<h3>使用指南</h3>
<ul>
    <li>在 windows 系统中<a href="https://github.com/socia1ca3t/timepill-backup-17/releases/download/Lasted/timepill-backup.exe">下载 timepill-backup.exe</a> 文件 </li>
    <li>双击运行，然后在浏览器地址框输入：http://localhost:10086/</li>
    <li>使用胶囊账号和密码登录</li>
</ul>


<h3>技术与实践</h3>
<ul>
    <li>环境：JDK17</li>
    <li>前端：响应式 BulmaCSS + jQuery + html2canvas</li>
    <li>后端：Springboot3 + SpringSecurity6 + thymeleaf + caffeine</li>
    <li>可基于 GraalVM Native Image 将此项目构建成为独立的可执行文件，执行该文件时不需要提前配置任何环境依赖。</li>
    <li>基于面向对象的编程范式，实现了系统功能的模块化和组件化。</li>
    <li>运用了策略模式、模板模式和观察者模式，实现了对下载进度的实时观测，并配合SSE异步通信技术，实现了前端页面动态展示下载进度的功能。</li>
    <li>运用了生产者消费者模式，实现了图片下载的异步处理。</li>
    <li>对线程安全性，对象的可见性，以及对象的安全发布进行了实践，以确保观察者可以实时接收到下载任务状态更新的通知。</li>
</ul>

<h3>设计思路</h3>
两大功能：
<li>登录后展示个人信息、所有日记本及其中的所有日记</li>
<li>一键截图、一键下载备份</li>
<br/>
图片类型：
<li>按在HTML中显示的位置划分：个人主页上的图片、日记中的图片</li>
<li>按图片归属划分：用户头像、日记本封面、日记图片</li>
<br/>
主要流程：
<li>首先，通过胶囊接口获取到图片链接</li>
<li>然后，根据相应的功能点设置图片的 src 路径，同时得到所有需要下载的图片信息</li>
<li>接着，多线程下载图片，且异步的渲染 HTML 模板</li>
<li>最后，前端直接展示，或将所有文件压缩成 ZIP 文件提供下载</li>
<br/>
核心接口：
<li>ImgPathProducer： 根据图片的类型，提供图片下载时的绝对路径，以及图片在 HTML 中 img 标签的 src 路径</li>
<li>ImgPathSetter： 设置所有图片的 src 路径，同时收集所有需要下载的图片的信息</li>
<li>ImgDownloader： 下载图片</li>
<li>Backuper：将以上 3 类接口及其它逻辑组合起来，提供备份功能</li>

<h3>问题与解决</h3>
<ul>
    <li>问题：在Springboot2.5的版本中，对象中的 @Cacheable 注解未生效，导致缓存失败。</li>
    <li>解决：追踪AOP代理对象注入机制的源码，并参考 Github 中 Spring 官方的 issue，使用 @Lazy 注解延迟注入。
        详见我在 stackoverflow 提出的问题。<a href="https://stackoverflow.com/questions/76350019/the-cglib-enhancement-can-not-work-after-i-introduce-the-spring-boot-starter-dat">点击查看</a>。</li>
    <li>问题：部分浏览器，在息屏或切换到后台时，会断开浏览器的网络连接，导致下载进度无法更新。</li>
    <li>解决：前端每隔 3S 检测SSE连接是否关闭，若非正常关闭，则重新发起 SSE 连接。</li>
    <li>问题：CDN 服务器使用了图片防盗链，无法直接在网页中直接引用图片链接。</li>
    <li>解决：下载图片资源至本地之后，再提供展示。</li>
    <li>问题：CDN 图片服务器启用 CC 防护，大量频繁请求会被限流，导致下载失败。</li>
    <li>解决：任务的消费者使用了延时队列，任务失败后，设置该任务的重试延迟时间，并重新加入队列。</li>
    <li>问题：构建的 exe 原生镜像文件运行时，若 HTML 模板渲染所需要的对象未注册为反射元数据，thymeleaf 渲染模板时会卡住，不会抛出任何异常或提示信息。</li>
    <li>解决：使用 GraalVM 自带的 tracing agent 收集编译所需元数据。</li>
</ul>


<h3>使用 GraalVM 自带的 tracing agent 收集编译所需元数据</h3>
<ul>
    <li>更多配置，请参考<a href="https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/">官方指南</a></li>
    <li>在命令行中启动编译好的 JAR 文件：java -agentlib:native-image-agent=config-output-dir=\your\path\collection,config-write-period-secs=30,config-write-initial-delay-secs=5 -jar \your\path\timepill-backup-0.0.1-SNAPSHOT.jar</li>
    <li>项目启动后将所有功能执行一次，确保编译所需的所有元数据被添加至：\your\path\collection </li>
    <li>将 tracing agent 生成的文件放置在项目的 META-INF\native-image\ 路径下，构建镜像时会自动使用该路径内的配置文件。</li>
</ul>


<h3>一些思考</h3>

程序设计就像管理公司，面向对象通过给这家公司设置不同的部门（模块接口）与岗位（具体实现），解决了系统的复杂性问题；
而函数式编程通过给每个岗位（具体实现）配备电脑，提高了编码的效率。
