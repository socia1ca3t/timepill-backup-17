<h1>胶囊日记一键备份项目</h1>

本项目基于胶囊官方提供的开放接口，实现了胶囊日记一键备份下载功能。备份文件的格式为HTML离线文档。

<h3>功能介绍</h3>
<ul>
    <li>实时提示下载进度。</li>
    <li>一键截图单本日记。</li>
    <li>一键下载单本日记或所有日记。</li>
</ul>

![example](https://github.com/socia1ca3t/timepill-backup/assets/147909308/a681bc57-4331-4573-97e8-d6437d2cb82a)

<h3>技术与实践</h3>
<ul>
    <li>环境：JDK17</li>
    <li>前端：响应式 BulmaCSS + jQuery + html2canvas</li>
    <li>后端：Springboot3 + SpringSecurity6 + thymeleaf + jpa + h2 + caffeine</li>
    <li>可基于 GraalVM Native Image 将此项目构建成为独立的可执行文件。</li>
    <li>基于面向对象的编程范式，实现了系统功能的模块化和组件化。</li>
    <li>运用了策略模式、模板模式和观察者模式，实现了对下载进度的实时观测，并配合SSE异步通信技术，实现了前端页面动态展示下载进度的功能。</li>
    <li>运用了生产者消费者模式，实现了图片下载的异步处理。</li>
    <li>对线程安全性，对象的可见性，以及对象的安全发布进行了实践，以确保观察者可以实时接收到下载任务状态更新的通知。</li>
</ul>


<h3>问题与解决</h3>
<ul>
    <li>问题：在Springboot2.5的版本中，对象中的 @Cacheable 注解未生效，导致缓存失败。</li>
    <li>解决：追踪AOP代理对象注入机制的源码，并参考 Github 中 Spring 官方的 issue，使用 @Lazy 注解延迟注入。
        详见我在 stackoverflow 提出的问题。<a href="https://stackoverflow.com/questions/76350019/the-cglib-enhancement-can-not-work-after-i-introduce-the-spring-boot-starter-dat">点击查看</a>。</li>
    <li>问题：部分浏览器，在息屏或切换到后台时，会断开浏览器的网络连接，导致下载进度无法更新。</li>
    <li>解决：前端每隔 3S 检测SSE连接是否关闭，若非正常关闭，则重新发起 SSE 连接。</li>
    <li>问题：CDN图片服务器启用CC防护，大量频繁请求会被限流，导致下载失败。</li>
    <li>解决：任务的消费者使用了延时队列，任务失败后，设置该任务的重试延迟时间，并重新加入队列。</li>
</ul>


<h3>一些思考</h3>

程序设计就像管理公司，面向对象通过给这家公司设置不同的部门（模块接口）与岗位（具体实现），解决了系统的复杂性问题；
而函数式编程通过给每个岗位（具体实现）配备电脑，提高了编码的效率。
