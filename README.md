# PluginGenerator

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![MCreator](https://img.shields.io/badge/MCreator-2026.1+-blue.svg)](https://mcreator.net/)

**PluginGenerator** – 一个专门为 [MCreator](https://mcreator.net/) 设计的插件生成器。  
**PluginGenerator** – A plugin generator specifically designed for MCreator.

它可以快速生成 MCreator 插件的基础结构，帮助开发者专注于业务逻辑而非重复的样板代码。

## ✨ 特性 (Features)

- 🚀 **一键生成** – 基于模板生成完整的 MCreator 插件项目结构
- 📦 **自动配置** – 自动生成 `plugin.json`、Gradle 构建脚本、主类文件
- 🧩 **模块化设计** – 支持生成生成器模块、元素类型扩展、自定义 UI 组件
- ⚡ **开箱即用** – 生成的代码可直接导入 MCreator 进行编译和测试
- 🛠️ **可扩展模板** – 允许用户自定义 Velocity / FreeMarker 模板

## 📥 安装与使用 (Installation & Usage)

### 前置要求
- Java 17 或更高版本
- MCreator 2024.3+（开发插件所需）

### 从源码构建
```bash
git clone https://github.com/yourusername/PluginGenerator.git
cd PluginGenerator
./gradlew build
