# Selenium Server



## 版本更新

**2018.3.23**

1. 脚本父类BaseScript中，增加了executeScript方法，可调用与当前脚本同属一个测试系统的其他脚本，如下：

```
/**
 * 动态调用其他脚本，只能调用与当前脚本同属一个系统的其他脚本，不能跨系统调用
 * @param className 类名，ShortName，不含包名
 * @param method 方法名
 * @param params 执行method方法时传入的参数数组
 * @return 返回调用的脚本执行的返回值，为Boolean
 * @throws Exception 调用脚本失败
 */
public Boolean executeScript(String className, String method, Object[] params) {
}
```



## 1. 概述

Selenium Manager是基于Selenium开发的B/S架构的自动化测试系统，可以对不同的系统，使用不同的测试环境进行测试，支持测试脚本的热部署。

注：文中提到的**系统名**，均指测试目标系统的英文名称，其中只可包含英文字母和数字，不允许使用中文、空格和特殊字符等，不同系统的名称不可重复。



## 2. 配置
配置主要内容是定义测试用到的多个浏览器及其驱动，定义各种测试场景。配置文件路径为`/src/main/resources/config.properties`

- 浏览器和驱动，参数如下
  - `path.浏览器名`
    浏览器本地调用路径（Windows下的IE和MacOS下的所有浏览器无需设置此参数）
  - `driver.浏览器名`
    浏览器驱动名称，驱动文件在/src/main/resources/driver目录下

  例如：
```properties
#Windows系统中，类似如下格式
driver.chrome=chromedriver.exe
path.chrome=C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe
```

- 测试场景
  测试场景表示用什么浏览器，浏览器窗口多大等，参数如下：
```properties
#chrome前缀表示测试场景名称，由用户自定义，通过服务调用测试任务时的参数之一。

#测试环境的说明，用于生成日志
chrome.comment=Chrome测试

#测试用浏览器类型，取值包括chrome，firefox，ie
chrome.browser=chrome

#浏览器窗口大小，值可以是【max】或【宽度,高度】，分隔符为半角逗号
chrome.browser.size=1366,768
```



## 3. 部署
使用JDK1.8编译后，直接用Tomcat8部署即可。



## 4. 开发指南
测试用例的开发包括两部分，一是开发**测试脚本**，每个测试脚本为一个Java类文件，对应某个测试系统中的一类系统功能，其中按照功能点不同，划分为多个方法，例如：通用类中包括登陆和注销方法，工作流类中包括创建流程方法和流程审批方法。二是配置**测试用例**，测试脚本中的方法不能被平台作为测试任务调用，而是作为一个个独立的步骤，成为了组成系统测试任务的最小单位，用户通过配置，将这些可复用的步骤串行结合成测试用例，每个测试用例用来应对一个业务场景。例如：创建工作流用例，由【通用类.登陆方法 -> 工作流类.创建流程方法 -> 通用类.注销方法】组成，每次测试流程创建功能，必须调用整个创建工作流测试用例。



### 4.1 测试脚本

测试脚本通常按照系统功能类别区分为不同的Java类，为了提高脚本开发效率，我们进行如下约定。

#### 4.1.1 规范

为了规范开发，进行如下约定：
- 脚本用java开发，文件后缀为`.java`
- 脚本文件名与类名一致，同文件夹下的类不可重名
- 第一行代码中的包名必须为`package cn.o.build.系统名;`
- 脚本必须继承`BaseScript`类，从而调用一些公用属性和方法
- 脚本中的方法返回值为Boolean，返回true时，后续脚本会被继续执行，返回false，则中断这个测试用例，后续脚本不执行
- 父类`BaseScript`包含如下属性和方法：
    - 属性（所有属性都有getter、setter）
        - `webDriver [WebDriver]` WebDriver对象
        - `url [String]` 网站地址
        - `log [TestLog]` 日志对象
        - `SUCCESS [Boolean]` true
        - `FAIL [Boolean]` false
    - 方法
        - `waitSeconds(Integer i)` 等待`i`秒
        - `addLog(String str)` 增加一条内容为`str`的日志
        - `addErrorLog(String str)` 增加一条内容为`str`的错误日志
        - `executeScript(String className, String method, Object[] param)` 调用同系统的其他脚本


> 每个测试请求包括至少一个串行的测试用例（例如：可以将创建工作流用例和审批工作流用例串行为一个请求），每个请求被执行后，都会生成一个日志文件，测试用例的方法被依次执行时，其中通过addLog和addErrorLog记录的消息都会添加到这个日志文件中，日志文件是txt文件。但是，只要这个测试请求执行时，调用了一次addErrorLog方法后，整个日志文件会被定义为错误日志，文件名以`.error.txt`结尾。



#### 4.1.2 示例

下面是一个测试脚本例子，测试登陆功能

```java
package cn.o.build.scm;

import org.openqa.selenium.By;
import cn.o.test.model.BaseScript;

public class Common extends BaseScript {
    public Boolean login() {
        this.addLog("输入用户名");
        this.getWebDriver().findElement(By.id("account")).click();
        this.getWebDriver().findElement(By.id("account")).clear();
        this.getWebDriver().findElement(By.id("account")).sendKeys("administrator");
        this.addLog("输入密码");
        this.getWebDriver().findElement(By.id("passwordid")).clear();
        this.getWebDriver().findElement(By.id("passwordid")).sendKeys("123456");
        this.addLog("点击登陆按钮");
        this.getWebDriver().findElement(By.id("btnsubmit")).click();
        try {
            // 实例化一个等待过程
            WebDriverWait wait = new WebDriverWait(this.getWebDriver(), 5);
            // 等待页面跳转，直到页面的title中包含"社会管理综合治理信息平台"字符串
            wait.until(ExpectedConditions.titleContains("社会管理综合治理信息平台"));
            // 在页面中查找注销按钮是否存在
            WebElement ele = this.getWebDriver().findElement(By.id("btn-logout"));
            this.addLog("登陆成功");
            return SUCCESS;
        } catch (Exception e) {
            // 此处捕获的异常是wait超时或者查找不到注销按钮，说明登录失败
            // 改为查找登录页面消息反馈DOM
            WebElement ele = this.getWebDriver().findElement(By.id("info_prompt"));
            // 将消息反馈DOM中的文本记录在日志中，例如："账号不存在"，"密码错误"
            this.addLog("登陆失败" + ele.getText());
            return FAIL;
        }
    }
}
```



#### 4.1.3 动态编译

项目发布时，脚本文件不会被编译为class文件，而是作为资源文件被存放在`/TomcatRoot/WEB-INF/classes/source`中，执行测试用例过程中，当需要调用测试脚本中的类和方法时，系统会将脚本中的代码进行实时编译，然后执行。这就表示，我们可以在服务器上对脚本进行实时修改或替换，而无需对整个工程进行重新发布或重启。

> 实际上，由于每次都进行编译效率较低，所以系统调用一个脚本后，会将其编译后的类缓存起来，当再次调用同名称的脚本时，会比较两次调用的脚本的Hash值，如果相同，则直接从内存中获取上次编译的类，而不会再次编译。



#### 4.1.4脚本编写

Selenium测试工作都是基于操作页面DOM的，所以脚本编写主要分为**Locator**、**操作DOM**和**等待**三部分内容。

##### 4.1.4.1 Locator

Locator是WebDriver用于定位DOM的方式，主要方法如下：

| 方式            | 代码                                       |
| ------------- | ---------------------------------------- |
| **id**        | **driver.findElement(By.id("id的值"));**   |
| **xpath**     | **driver.findElement(By.xpath("xpath表达式"));** |
| **jquery表达式** | **Js.executeScript("return jQuery.find("jquery表达式")")** |
| name          | driver.findElement(By.name("name的值"));   |
| class         | driver.findElement(By.className("class属性")); |
| css           | driver.findElement(By.cssSelector("css表达式")); |
| tagname       | driver.findElement(By.tagName("标签名称"));  |
| 链接全部文字        | driver.findElement(By.linkText("链接的全部文字")); |
| 链接部分文字        | driver.findElement(By.partialLinkText("链接的部分文字")); |

选取locator的优先级是

> ID > Name > CSS > XPath

示例：

```java
// id
this.getWebDriver().findElement(By.id("flowTitle"));

// xpath
this.getWebDriver().findElement(By.xpath("//ul[@id='main-content-menu']/li[2]/div[2]"));

// 以上方法的返回值是一个WebElement对象
```



##### 4.1.4.2 操作DOM

DOM的操作比较多，只举两个常用例子：

```java
// 点击id为login的dom
this.getWebDriver().findElement(By.id("login")).click();

// 查找性别选择select
WebElement element = this.getWebDriver().findElement(By.id("select-gender"));
// 点击select
element.click();
// 将dom包装为select对象，并选择文本为"男"的选项
new Select(element).selectByVisibleText("男");
```



##### 4.1.4.3 等待

如果让脚本按照<查找DOM-操作DOM-查找DOM...>的顺序执行下去，大部分情况是会报错的，因为在操作DOM之后，很可能需要从后台获取数据生成DOM、或者需要等待交互动画结束，所以经常需要进行等待。等待一般包括**三种（比网上常见的多1种）**，**显式等待**、**隐式等待**和**强制等待**。



###### 显式等待

显示等待是指显式声明等到某个状态出现，或者超时，例如：

```java
// 声明一个等待对象，最大等待5秒
WebDriverWait wait = new WebDriverWait(this.getWebDriver(), 5);
// 等待id为addNewTable的DOM可见
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNewTable")));

// 等待通过则会继续向下运行代码，不通过的话会抛出异常
```

等待条件主要包括下面几种场景，通常，其中的带Located的条件，接受参数为locator对象，即 `By.xxx`；而没有Located的接受参数通常为 `WebElement `对象或一些Java的基础类型：

| 条件                               | 含义                                       |
| -------------------------------- | ---------------------------------------- |
| titleIs                          | 判断当前页面的title是否等于预期                       |
| titleContains                    | 判断当前页面的title是否包含预期字符串                    |
| presenceOfElementLocated         | 判断某个元素是否被加到了dom树里，并不代表该元素一定可见            |
| visibilityOfElementLocated       | 判断某个元素是否可见，可见代表元素非隐藏，并且元素的宽和高都不等于0       |
| visibilityOf                     | 上面的方法要传入locator，这个方法直接传定位到的element       |
| presenceOfAllElementsLocated     | 判断是否至少有1个元素存在于dom树中。举个例子，如果页面上有n个元素的class都是'col-md-3'，那么只要有1个元素存在，这个方法就返回True |
| textToBePresentInElement         | 判断某个元素中的text是否 包含了预期的字符串                 |
| textToBePresentInElementValue    | 判断某个元素中的value属性是否包含了预期的字符串               |
| frameToBeAvailableAndSwitchToIt  | 判断该frame是否可以switch进去，如果可以的话，返回True并且switch进去，否则返回False |
| invisibilityOfElementLocated     | 判断某个元素中是否不存在于dom树或不可见                    |
| elementToBeClickable             | 判断某个元素中是否可见并且是enable的，这样的话才叫clickable    |
| stalenessOf                      | 等某个元素从dom树中移除，注意，这个方法也是返回True或False      |
| elementToBeSelected              | 判断某个元素是否被选中了,一般用在下拉列表                    |
| elementLocatedToBeSelected       | 和上面一样，只是传入locator                        |
| elementSelectionStateToBe        | 判断某个元素的选中状态是否符合预期                        |
| elementLocatedSelectionStateToBe | 跟上面的方法作用一样，只是上面的方法传入定位到的element，而这个方法传入locator |
| alertIsPresent                   | 判断页面上是否存在alert                           |



###### 隐式等待

隐式等待是在**全局**设置所有findElement的等待超时时间，设置一次即可，直到需要修改，才再次设置，代码如下：

```java
// 超时时间为10妙
this.getWebDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
```



###### 强制等待

强制等待不是Selenium的等待方式，而是我们自定义的线程等待，可以用来进行强制等待，例如：触发弹窗显示的瞬间，DOM已经完成了渲染，但动画结束前，还无法进行操作，所以需要强制等待，代码如下：

```java
// 参数单位为毫秒
Thread.sleep(1000);		// 等待1秒

// BaseScript中封装了这个方法，参数单位为秒
this.waitSeconds(1);	// 等待1秒
```



### 4.2 测试用例

测试用例现已可在系统设置模块中，通过GUI界面进行配置，这里大致讲解一下测试用例的数据结构。

测试任务配置文件储存位置是`/TomcatRoot/WEB-INF/classes/system.json`，基本格式如下

```
[
  {
    "name": "scm",
    "comment": "社管2.5",
    "url": "http://192.168.2.224:8081/",
    "jars": "guava-24.0-jre.jar",
    "tasks": [
      {
        "comment": "创建工作流",
        "steps": [
          "Common.login",
          "Workflow.create",
          "Common.logout"
        ],
        "params": [
          ["admin", "123456"],
          null,
          null
        ]
      },
      {
        "comment": "流程审批",
        "steps": [
          "Common.login",
          "Workflow.approve"
        ],
        "params": [
          ["test", "123456"],
          null
        ]
      }
    ]
  },
  {
    ......
  }
]
```
- 测试用例需要按系统不同进行分组配置，整个`system.json`是一个数组，数组的每个元素代表一个系统。其中的`name: scm`表示**系统名**，**系统名**需要与测试脚本放置的文件夹名称保持一致。每个系统除了`name`，还包括`comment`、`url`、`jars`、`tasks`共4个属性，含义如下：
  - comment：系统说明，生成测试日志时会用到
  - url：系统的访问地址
  - jars：测试脚本编译中需要引入的依赖包
  - tasks：测试用例，包含`comment`、`steps`、`params`三个属性，分别表示：
    - comment：测试用例的说明
    - steps：字符串组成的数组，表示测试用例依次调用的测试脚本的方法，数组每个元素的格式为【类名.方法名】，指向测试脚本中声明的类和方法
    - params：二维数组，数组长度与steps相同，作为调用steps中方法时传入的参数，二维数组中的元素为字符串，无法直接传入对象，如需使用对象，需要在脚本中根据字符串解析，




## 5. API

服务端程序提供的API清单，可跳过不看

- 功能：执行测试任务
```
URL：/test/run
参数: param [String] - 需要将一个参数对象JSON化后作为值，参数对象格式如下
param: JSON.stringify({
    // 系统名，需要与task.json中的一级节点系统名一致
    system: 'scm',
    // 测试环境名，与config.properties中的测试环境名一致
    config: 'chrome',
    // 串行任务，数组中每个元素代表一个任务
    tasks: [
        {
          // 任务名，在task.json中的二级节点中选取
          name: "WfCreate",
          // 步骤执行的参数，是个二维数组，一个任务有多个步骤，每个步骤对应数组中的一个数组元素，表示该步骤执行时，将数组元素作为参数arguments[]传入
          // 例如：此处WfCreate任务有3个步骤，其中第一步骤为登陆，
          // 所以将账号密码写成一个数组["administrator", "shzl2018"]，传入登陆方法
          params:[["administrator", "shzl2018"], null, null],
        },
        {
          name: "WfApprove",
          params:[["sq001", "123456"], null, null],
        }
    ]
})
返回值：是否成功，错误信息
```
- 功能：列出所有测试系统信息

```
URL：/test/system/list
参数：无
返回值：List<System>
```

- 保存测试系统信息

```
URL：/test/system/save
参数：无
返回值：无
```
- 功能：列出所有保存在服务端的模板名

```
URL：/test/template/list
参数：无
返回值：List<String>
```

- 功能：保存模板到服务器

```
URL：/test/template/save
参数：
name [String] - 模板名
content [String] - 模板内容，是一个JSON字符串
返回值：String - 模板名
```

- 功能：获取一个模板的内容

```
URL：/test/template/get
参数：name [String] - 模板名
返回值：String - 模板内容
```

- 功能：重命名模板

```
URL：/test/template/rename
参数：
oldName [String] - 旧模板名
newName [String] - 新模板名
返回值：String - 新模板名
```

- 功能：删除模板

```
URL：/test/template/delete
参数：name [String] - 模板名称
返回值：String - 删除的模板名
```

- 功能：列出一天的日志

```
URL：/test/log/list
参数：date [String] - 日期
返回值：List<String> - 日志的相对地址列表
```

- 功能：保存并发请求的总日志

```
URL：/test/log/save/summary
参数：
startTime [String] - 开始时间
endTime [String] - 结束时间
content [String] - 日志内容
返回值：List<String> - 日志的相对地址
```

- 功能：下载日志

```
URL：/test/log/download
参数：path [String] - 日志相对地址
返回值：文件输出流
```

- 功能：列出所有脚本

```
URL：/test/source/list
参数：system [String] - 测试系统名
返回值：List<Source> - 脚本列表信息
```

- 功能：获取脚本源码

```
URL：/test/source/get
参数：
system [String] - 测试系统名
name [String] - 测试脚本名，不包含java后缀
返回值：文件输出流
```

- 保存脚本

```
URL：/test/source/save
参数：
system [String] - 测试系统名
name [String] - 测试脚本名，不包含java后缀
code [String] - 脚本源码
返回值：文件输出流
```



## 6. 日志

日志分为两种，单请求和并发请求

- 单请求
  - 每个测试请求可能对应多个串行测试用例，每个测试请求执行完成之后，都会生成一条日志，用来记录执行情况。
  - 日志文件的保存路径为`/TomcatRoot/logs/年/月-日/时-分-秒-毫秒-系统名-测试场景-任务名-流水号.txt`
  - 如果脚本执行出现异常或执行过至少一次addErrorLog方法，则日志文件名中会加入`.error`的字样，例如：`/SeleniumManager/logs/2018/03-06/11-33-39-000601-scm-scm01-WfCreate-0.error.txt`
- 并发请求
  - 并发请求包括多个单请求，其中每个请求会按照上述规则单独生成一份日志
  - 所有请求执行完成后，会生成一份总览日志，日志保存路径为`/TomcatRoot/logs/年/summary/开始时间-结束时间.txt`




## 7. 并发

并发任务的最优做法是把所有任务一次性提交到服务端，由服务端按照最大并发数多线程运行，并由在前端监听请求执行进度。但由于时间比较紧，所以选择次优方案，由前端将所有任务按照自定义的最大并发数分成若干个请求，依次发送到服务端。这样做的缺点是，所有任务运行完成前，任务管理页面不可关闭，否则后续任务会被中断无法提交。后续有时间再做修改，将并发任务交给后台控制。




## 8. 系统功能 

系统现在主要分成任务管理、测试日志和系统设置三个模块。

#### 8.1 任务管理

- 新增请求

  增加一个测试请求，配置请求的测试系统、测试环境、用例及对应的参数，增加的请求状态为**未提交**。面板上的所有请求会按照测试的目标系统不同，而进行分组，每个分组可以进行收起和展开。

- 保存

  将面板上的所有任请求存为一个模板，保存时，可选择已有模板进行覆盖，也可以自定义模板的名称，当自定义的模板名称与已有模板相同时，也会进行覆盖，覆盖后，原模板内容不可恢复。

- 模板

  从已有模板中选择一项进行加载，加载时会清空当前面板上已有的请求。

- 清空

  清空当前请求列表。

- 导出

  将请求清单中所有请求导出为字符串，字符串会被自动复制到剪贴板，可以保存后异地导入或下次导入。

- 导入

  可以将导出的字符串进行导入，导入后请求列表会添加相关请求。

- 运行

  运行分为单个运行和全部运行，状态为**未提交**、**错误**、**失败**的请求可以运行，状态为**完成**、**运行中**的请求不可运行。

  单个运行功能在任务卡片上，点击绿色的Play图标，可运行相应的请求。

  全部运行功能在页面右上方，点击全部运行按钮，可运行所有可运行的请求（状态不为完成和运行中的请求）。

  运行过程中，请求状态会修改为**运行中**，测试请求运行完成后，可以查看日志，并且状态会被修改为**失败**、**错误**或**完成**。**失败**和**错误**的请求可以再次编辑和运行。完成的请求不可以直接编辑和运行，但可以进行重置，重置后的任务会回到**未提交**的状态，然后就可以进行编辑和运行。





#### 8.2 测试日志

测试日志模块中，可按照日期查询某天生成的请求日志，其中有错误信息的日志会被标注为红色背景，后缀为 `.error.txt`。



#### 8.3 系统设置

系统设置模块中，可以定义多个测试目标系统和每个系统中的测试脚本，并将脚本灵活组合，定义出不同的测试用例，还可以对测试用例中调用的方法设置默认参数值。主要包括以下功能：

- 新增系统
- 编辑系统
- 新增脚本
- 编辑脚本
- 新增用例
- 编辑用例
- 删除用例

**注：**

**1. GUI界面不提供删除系统和删除脚本功能，如需删除，远程登陆服务器，修改配置文件system.json**

**2. GUI界面无法设置测试环境config.properties，因为测试环境主要设置的是服务器上的浏览器安装路径，所以在部署服务器环境时进行设置，比在GUI界面设置的准确度高，而且一次设置，后续无需修改，所以不提供配置功能**