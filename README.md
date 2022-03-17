基于https://github.com/starxg/mybatis-log-plugin-free v1.20修改    
修复了参数为null时的参数拼接错位问题

```sql
==>  Preparing:
UPDATE mp_user
SET name=?
WHERE id = ?
  AND name = ?
  AND age = ?
  AND email = ?
  AND deleted = 0 
  ==> Parameters: null, 1(Long), 张三(String), 18(Integer), x@y.com(String)
  
  这样的语句会被错误拼接
```
