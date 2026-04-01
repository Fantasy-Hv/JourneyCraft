# JourneyCraft 后端服务

---

## 项目模块目录结构

### 1. 根目录结构

```
JourneyCraft/
├── .mvn/                    # Maven 配置目录
├── src/                     # 源代码目录
│   ├── main/                # 主代码
│   └── test/                # 测试代码
├── pom.xml                  # Maven 项目配置
├── README.md                # 项目说明
└── 模块划分.md               # 详细模块设计文档
```

### 2. 源码目录结构

| 目录 | 说明 |
|------|------|
| api | 定义该模块为其他模块暴露的接口 |
| dto | 定义其他模块调用模块接口时传递的数据对象 |
| vo/reqvo | 请求视图对象，定义 HTTP 请求参数解析后的 body 对象 |
| vo/rspvo | 响应视图对象，定义返回给前端的响应数据结构 |

```
src/main/java/org/dsgroup/journeycraft/
├── auth/                    # 认证鉴权模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象 (Request VO)
│       └── rspvo/           # 响应视图对象 (Response VO)
│
├── user/                    # 用户模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── scenic/                  # 景点模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── navigation/              # 导航模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── recommend/               # 推荐模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── group/                   # 多人协同模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── diary/                   # 日记模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── expense/                 # 账单模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── favorite/                # 收藏模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
├── history/                 # 历史模块
│   ├── api/                 # API 接口定义
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── service/             # 业务逻辑层
│   └── vo/                  # 视图对象
│       ├── reqvo/           # 请求视图对象
│       └── rspvo/           # 响应视图对象
│
└── file/                    # 文件存储模块
    ├── api/                 # API 接口定义
    ├── controller/          # REST 控制器
    ├── dto/                 # 数据传输对象
    ├── entity/              # 实体类
    ├── mapper/              # 数据访问层
    ├── service/             # 业务逻辑层
    └── vo/                  # 视图对象
        ├── reqvo/           # 请求视图对象
        └── rspvo/           # 响应视图对象
```

### 3. 各层职责说明

| 层级 | 目录 | 职责 |
|------|------|------|
| api | `api/` | 定义模块对外暴露的接口 |
| controller | `controller/` | 处理 HTTP 请求，参数校验，调用 service |
| dto | `dto/` | 数据传输对象，用于模块间数据传递 |
| entity | `entity/` | 数据库实体类，与表结构对应 |
| mapper | `mapper/` | 数据访问层，负责数据库操作 |
| service | `service/` | 业务逻辑层，核心业务处理 |
| vo/reqvo | `vo/reqvo/` | 请求视图对象，用于接收 HTTP 请求参数 |
| vo/rspvo | `vo/rspvo/` | 响应视图对象，用于返回 HTTP 响应数据 |

### 4. 模块依赖关系

```
                    ┌──────────────┐
                    │    user      │
                    │   (基础层)    │
                    └──────┬───────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    ┌────▼────┐      ┌────▼────┐      ┌─────▼─────┐
    │  auth   │      │favorite │      │  history  │
    │(基础层)  │      │(辅助层)  │      │  (辅助层)  │
    └─────────┘      └─────────┘      └───────────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    ┌────▼────┐      ┌────▼────┐      ┌─────▼─────┐
    │ scenic  │      │  file   │      │    ...    │
    │(核心层)  │      │(基础层)  │      │           │
    └────┬────┘      └─────────┘      └───────────┘
         │
    ┌────┴────────────────────┐
    │                         │
┌───▼───┐  ┌────────┐   ┌────▼────┐
│navigation│ │recommend│   │ group   │
│(核心层)  │ │(核心层)  │   │(核心层)  │
└───────┘  └────────┘   └─────────┘
                           │
              ┌────────────┼────────────┐
              │                         │
         ┌────▼────┐              ┌─────▼─────┐
         │  diary  │              │  expense  │
         │(核心层)  │              │(核心层)   │
         └─────────┘              └───────────┘
```

### 5. 技术栈

- **框架**: Spring Boot 3.2.x
- **ORM**: MyBatis-Plus 3.5.x
- **认证**: Spring Security + JWT
- **数据库**: MySQL 8.0 / MongoDB 7.0
- **对象存储**: MinIO
- **API 文档**: Knife4j (Swagger UI)

### 6. 开发环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- MongoDB 7.0+
- MinIO (可选，用于文件存储)

### 7. 快速开始

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd JourneyCraft

# 编译项目
mvn clean install

# 运行项目
mvn spring-boot:run
```

### 8. API 文档

启动项目后，访问以下地址查看 API 文档：
- Swagger UI: `http://localhost:8080/doc.html`
