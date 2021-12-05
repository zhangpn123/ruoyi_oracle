import com.baobiao.Application;
import com.baobiao.project.report.customquert.service.CustomqueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @ClassName: Demo
 * @Description:
 * @Author: zhangpuning
 * @Date 2021/9/22
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Demo {

    @Autowired
    private CustomqueryService customqueryService;

    @Test
    public void test(){
        List list = customqueryService.selectList("select *  from SYS_DICT_DATA where DICT_TYPE  = 'custom_query'");
        System.out.println(list);
    }

}
