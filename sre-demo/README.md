# 海底捞订单服务 (SRE Demo)

这是一个用于 QoderWake SRE 演示的 Spring Boot 项目。

## 已知 Bug

### [BUG] NullPointerException in OrderService.getOrderDetail

**现象**: 调用 `GET /api/orders/999`（不存在的订单ID）时返回 500 Internal Server Error

**根因**: `OrderService.getOrderDetail()` 方法中，`findById()` 返回 null 后直接调用了对象方法，未做空值检查。

**复现步骤**:
```bash
# 启动服务
./mvnw spring-boot:run

# 触发 Bug
curl http://localhost:8080/api/orders/999
```

**位置**: `src/main/java/com/haidilao/service/OrderService.java:42`

## 启动方式

```bash
cd sre-demo
./mvnw spring-boot:run
```

## 技术栈

- Java 17
- Spring Boot 3.3.0
- Spring Data JPA
- H2 Database (内存模式)
