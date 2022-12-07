package com.andrew;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONUtil;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.submitted.permissions.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * to test
 *
 * @author tongwenjin
 * @since 2022/11/18
 */
public class TestMybatis {

  private static SqlSessionManager session;

  @BeforeAll
  static void init() throws Exception {
//    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
//    SqlSessionFactory factory = builder.build(Resources.getResourceAsReader("com/andrew/mybatis-config.xml"));


     session =  SqlSessionManager
       .newInstance(Resources.getResourceAsReader("com/andrew/mybatis-config.xml"));
//    session.startManagedSession(true);
  }


  @Test
  public void testSessionCache() throws Exception {
    ZaRoomMapper mapper = session.getMapper(ZaRoomMapper.class);

    ZaRoom param = new ZaRoom();
    List<ZaRoom> zaRooms = mapper.selectList(param);


    Field factoryField = session.getClass().getDeclaredField("sqlSessionFactory");
    factoryField.setAccessible(true);
    SqlSessionFactory factory = (SqlSessionFactory)factoryField.get(session);

    SqlSession session1 = factory.openSession();
    List<ZaRoom> zaRooms1 = session1.getMapper(ZaRoomMapper.class).selectList(param);

    assertEquals(zaRooms1 , zaRooms , "mybatis 使用缓存 两次查询对象应该相等");
  }

  @Test
  public void test2() {
    System.out.println("test1 ");
  }
}
