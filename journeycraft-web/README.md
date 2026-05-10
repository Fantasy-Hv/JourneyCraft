# JourneyCraft 前端

## 本地运行

```bash
cd journeycraft-web
npm install
npm run dev
```

默认地址：

```text
http://localhost:5173/
```

## 构建

```bash
npm run build
```

## 联调模式

- 左侧栏和顶部栏都可以切换 `Mock / Real`
- `Mock` 模式适合脱离后端本地预览
- `Real` 模式会通过 Vite 代理访问 `http://localhost:8080/api`

## 页面结构

- 首页、景点、搜索、校园、景点详情
- 建筑 / 设施 / 美食列表与详情
- 用户中心、资料编辑、偏好设置、修改密码
- 导航、推荐、协同、小组、日记、账单、收藏、历史、文件的完整占位页

