# ACTS
[![Build Status](https://travis-ci.org/elseifer/sofa-acts.svg?branch=master)](https://travis-ci.org/elseifer/sofa-acts)
[![codecov](https://codecov.io/gh/elseifer/sofa-acts/branch/master/graph/badge.svg)](https://codecov.io/gh/elseifer/sofa-acts)
![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)

ACTS 是基于数据模型驱动测试引擎的的新一代测试框架，它的数据以 YAML 为载体，在此上构建基于数据模型的驱动引擎，适配 TestNg+SOFABoot 的测试上下文环境；支持高效、标准化构建用例，可视化编辑测试数据，精细化校验结果数据和自动清理 DB 数据，可以有效降低人工录入用例数据的成本，同时支持 API 重写提高测试代码的可扩展可复用性，提供特有注解提高测试代码编排的灵活性。
# 一、背景
保证代码质量、提高测试效率一直以来是测试人员关注的重点。目前现有测试框架依赖人工编写大量代码来组织测试数据、调度业务、控制校验点和清理DB数据，使得数据和代码耦合在一起，难以达到精细化校验，同时测试代码体积膨胀过快，可复用性降低，开发测试人员编写测试用例的效率难以提升。
为了提高测试用例编写效率和数据校验的完整、准确和精细化，蚂蚁金服基于 TestNg 研发了 ACTS 测试框架来解决上述问题。
# 二、功能简介
ACTS 提供了下面的能力：
## 2.1 数据可视化编辑
框架实现了测试数据与测试代码的分离，同时配套提供可视化编辑器 ACTS IDE，通过 ACTS IDE 可以快速地录入、查看和管理用例数据，有效减少重复性的数据准备代码。
## 2.2 精细化校验
为了提高方法返回值、DB 变更数据等期望数据值的填写效率和减少检验点遗漏，框架提供了预跑返填功能;在 ACTS 规则标签的标记下，实现期望 DB 数据、期望结果等精细化校验。
## 2.3 丰富的数据API
ACTS 数据自定义 API 接口封装于 ActsRuntimeContext 类里，可快速获取和设置自定义参数、用例入参、期望结果等，满足用户对用例数据的自定义操作；
## 2.4 自定义引擎各阶段
为了提高 ACTS 的灵活可扩展性，框架的 ActsTestBase 测试基类对外暴露各个执行阶段方法，包括 prepare，execute，check，clear 等，例如在测试类中，通过重写 process 方法可将整个测试脚本重新编排。
## 2.5 统一配置能力
配置文件中提供丰富的配置能力以定制化框架的个性需求。
# 三、快速开始
请查看文档中的[快速开始](https://www.sofastack.tech/sofa-acts/docs/GettingStarted)来了解如何快速上手使用 ACTS。
# 四、如何贡献
在贡献代码之前，请阅读[参与贡献](https://www.sofastack.tech/sofa-acts/docs/Contributing)来了解如何向 ACTS 贡献代码。
ACTS 的编译环境的要求为 JDK7 或者 JDK8，需要采用 Apache Maven 3.2.5 或者更高的版本进行编译。
# 五、感谢
ACTS 源于蚂蚁金服内部众多测试技术人员的经验总结，同时汲取了蚂蚁内部其他优秀白盒测试产品的设计优点，感谢这些工作者们的辛勤付出。
# 六、示例
ACTS 入门使用示例
# 七、文档
请参考 ACTS [用户使用手册](https://www.sofastack.tech/sofa-acts/docs/Usage-Ready)。