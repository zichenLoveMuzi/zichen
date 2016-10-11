package cloud.simple.hello;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cloud.simple.service.RuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api("规则相关的API")
public class SampleController  {
	
	@Autowired
	RuleService ruleService;
	
    /**
    * @url 
    * @brief 规则计算
    * @param Map<String, Object> {rules:['id1','id2',...],
    * 							   data:{
    * 										inputButtonId:'btn1',
    * 										inputRoleId:'roleId1',
    * 										businessData:{key1:'value1',key2:'value2',...}
    * 									}
    * 							   }
    * @return 
    * @throws 
    * @note 
    * @author chunzhao.li
    * @version v1.0.0.0
    * @date 2016年10月10日上午10:44:12
    */
    @ResponseBody
    @RequestMapping(value = "/calculation",method=RequestMethod.POST)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> rulesCalculation(@RequestBody Map<String, Object> param) {
    	System.out.println("================================规则过滤的输入条件======================================");
        System.out.println(param);
    	List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
    	if(param.get("rules")!=null){
			List<String> rules = (ArrayList<String>)param.get("rules");
    		if(!rules.isEmpty()){
    			Map<String, Object> message = (HashMap<String, Object>)param.get("data");
    			for(String ruleId:rules){
    				message.put("ruleId", ruleId);
    				result.add(ruleService.getRuleResult(message));
    			}
    		}
    	}
    	System.out.println("================================规则过滤的输出======================================");
    	System.out.println(result);
        return result;
    }
    
    /**
    * @url 
    * @brief 根据条件获取规则列表
    * @param caseKey:事件标识，全选为"ALL"; id:规则ID,全选为"ALL"
    * @return 
    * @throws 
    * @note 
    * @author chunzhao.li
    * @version v1.0.0.0
    * @date 2016年10月10日上午11:43:37
    */
    @ResponseBody
    @RequestMapping(value = "/rules/{caseKey}/{id}",method=RequestMethod.GET)
    public List<Document> loadRules(@PathVariable String caseKey,@PathVariable String id,HttpServletRequest request) {
    	Document filter = new Document();
    	if(caseKey!=null && !caseKey.isEmpty() && !"NULL".equals(caseKey.toUpperCase()) && !"ALL".equals(caseKey.toUpperCase())){
    		filter.append("caseKey", caseKey);
    	}
    	if(id!=null && !id.isEmpty() && !"NULL".equals(id.toUpperCase()) && !"ALL".equals(id.toUpperCase())){
    		filter.append("_id", id);
    	}
    	return ruleService.getRules(filter,null);
    }
    
    /**
     * @url 
     * @brief 根据CASEKEY获取规则下拉框
     * @param caseKey:事件标识
     * @return 
     * @throws 
     * @note 
     * @author chunzhao.li
     * @version v1.0.0.0
     * @date 2016年10月10日上午11:43:37
     */
     @ResponseBody
     @RequestMapping(value = "/rulelists/{caseKey}",method=RequestMethod.GET)
     public List<Document> loadRuleLists(@PathVariable String caseKey) {
     	Document filter = new Document().append("caseKey", caseKey);
     	Document projection = new Document().append("_id", 1).append("ruleName", 1);
     	return ruleService.getRules(filter,projection);
     }
    
    /**
     * @url 
     * @brief 保存规则
     * @param Document {_id:'',caseKey:'',ruleName:'',drl:''}
     * @return 
     * @throws 
     * @note 
     * @author chunzhao.li
     * @version v1.0.0.0
     * @date 2016年10月10日上午11:43:37
     */
     @ResponseBody
     @RequestMapping(value = "/rules",method=RequestMethod.POST)
     public void addRules(@RequestBody Document rule) {
     	ruleService.saveRules(rule);
     }
     
     /**
      * @url 
      * @brief 删除规则
      * @param Document {_id:'',caseKey:'',drl:''}
      * @return 
      * @throws 
      * @note 
      * @author chunzhao.li
      * @version v1.0.0.0
      * @date 2016年10月10日上午11:43:37
      */
      @ResponseBody
      @RequestMapping(value = "/rules",method=RequestMethod.DELETE)
      public void delRules(@RequestBody List<String> ids) {
      	ruleService.deleteRuleById(ids);
      }
}
