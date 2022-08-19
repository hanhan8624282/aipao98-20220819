package com.ecc.aipao98.dao;

import com.ecc.aipao98.pojo.SmsLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sunyc
 * @create 2022-08-16 8:49
 */
@Repository
public interface SmsLogDao {
    /**
    *@Description: 保存发送验证码流水
    *@Param:
    *@return:
    *@Author: your name
    *@date: 2022/8/16
    */
    @Transactional(rollbackFor = Exception.class)
    @Insert("insert into sms_log(create_time,cell_phone,ip,code,result_message,result_status,result_id)" +
            "values(#{createTime},#{cellPhone},#{ip},#{code},#{resultMessage},#{resultStatus},#{resultId})")
    int insert(SmsLog smsLog);

    /**
    *@Description: 每日获取的验证码次数
    *@Param: 
    *@return: 
    *@Author: your name
    *@date: 2022/8/16
    */
    @Select("select count(*),DATE_FORMAT(NOW(),'%Y-%m-%d')\n" +
            "  from sms_log a\n" +
            "where cell_phone=#{phone}\n" +
            "  and create_time>=DATE_FORMAT(NOW(),'%Y-%m-%d')")
    int sendCodeCount(String phone);
    /**
    *@Description: 验证码五分钟有效; 0=超过五分钟
    *@Param: 
    *@return: 
    *@Author: your name
    *@date: 2022/8/16
    */
    @Select("select count(*)\n" +
            "  from (\n" +
            "           select cell_phone, max(create_time) create_time\n" +
            "           from sms_log a\n" +
            "           where cell_phone = #{phone}\n" +
            "           group by cell_phone\n" +
            "       ) a\n" +
            "where a.create_time>= date_sub(now(),interval 5 minute )")
    int isFiveCount(String phone);
    /**
    *@Description: 手机验证码check
    *@Param:
    *@return:
    *@Author: your name
    *@date: 2022/8/17
    */
    @Select("select count(*) from sms_log where cell_phone=#{arg0} and code=#{arg1}")
    int checkPhoneFlag(String phone,String phonecode);
}
