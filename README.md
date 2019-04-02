# 概述
* hambomb-cache是基于spring开发的业务层缓存框架
* hambomb-cache采用预加载的方式在应用启动时将需要的缓存加载到缓存中。
* hambomb-cache可以通过配置实现多种部署方式，也可以支持多种缓存模式。
# 服务器模式
## 单机
用于开发的本地方式和简单应用下的单机部署方式。
## 集群
用于应用多实例集群部署。
# 缓存数据加载模式
## 全量加载
全量加载需要缓存的数据，支持单机和集群模式。
## 增量加载
增量加载从某个时间点未及时加载到分布式缓存中的数据和操作，支持集群模式。
# 存储模式
## 本地内存
* Guava Cache
 
 适合单机开发情况下。
* Caffeine Cache 
 
 适合单机部署的简单应用场景。
## NoSQL
* Redis
* Tair

## 自定义
可根据实际情况定义属于自己的存储模式,支持本地内存、Redis、Tari之外的方式。

# Key生成策略
* 本地策略
* Redis策略

# Key顺序策略
* 排列
* 组合
* 默认

# 快速开始
## 创建需要缓存的对象
* 实现CacheObjectMapper接口。
* 使用@CacheKey描述缓存对象的查询key。
```$xslt
@Mapper
public interface PersonSelfMapper extends CacheObjectMapper<Person> {


    @Select("select id,name,gender,age,height,weight,cardId,address " +
            "from t_person ")
    @Cachekey(findKeys = {"cardId"})
    @Override
    List<Person> selectAllCacheObject();
}
```
## 如何操作被缓存的数据
### @PreGetProcess
* 在查询被缓存数据的时候使用，被标识的方法在执行原逻辑之前会先查询缓存。
* 如果命中直接将缓存数据返回。
* 如果未命中则查询数据库，并将数据缓存到缓存中。

```$xslt
@PreGetProcess(args = {"#0"})
public Person getPersonByCardId(String cardId) {
    return personMapper.selectByCardId(cardId);
}
```
### @AfterUpdateProcess
* 在更新被缓存数据的时候使用，被标识的方式在执行原逻辑之前会将更新的数据刷新到缓存中。
```$xslt
@AfterUpdateProcess
public void modifyAddressById(Person modifyPerson) {

    personMapper.updateAddressById(modifyPerson.getId(),modifyPerson.getAddress());
}
```
### @AfterDeleteProcess
* 在删除被缓存数据的时候使用，被标识的方式在执行原逻辑之后会将删除的数据从缓存中删除。
```$xslt
@AfterDeleteProcess(cacheObjectClass = Person.class)
public void deletePersonById(Long id) {
    personMapper.deleteById(id);
}
```
### @AfterInsertProcess
* 在追加被缓存数据时使用，被标识的方法在执行原逻辑之后会将新增的数据刷新到缓存中。
```$xslt
@AfterInsertProcess
public void insertPerson(Person person) {
    personMapper.insert(person);
}
```