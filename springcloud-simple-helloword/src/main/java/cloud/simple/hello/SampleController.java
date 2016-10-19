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
import org.springframework.web.bind.annotation.RestController;

import cloud.simple.service.RuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api("规则引擎接口")
public class SampleController  {
	
	@Autowired
	RuleService ruleService;
	
    @RequestMapping(value = "/calculation",method=RequestMethod.POST)
    @SuppressWarnings("unchecked")
    @ApiOperation(value="规则过滤", notes="运行期点击按钮时进行规则过滤")
    @ApiImplicitParam(name = "param", 
    				  value = "需要计算的参数和使用的规则ID集合：{rules:['id1','id2',...],data:{inputButtonId:'btn1',inputRoleId:'roleId1',businessData:{key1:'value1',key2:'value2',...}}}", 
    				  required = true, 
    				  dataType = "Map")
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
    
    @RequestMapping(value = "/rules/{caseKey}/{id}",method=RequestMethod.GET)
    @ApiOperation(value="获取规则列表", notes="根据事件标识和规则ID获取规则列表，用于规则列表展示")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "caseKey", value = "事件标识",defaultValue = "ALL", required = true, dataType = "String"),
        @ApiImplicitParam(name = "id", value = "规则ID",defaultValue = "ALL", required = true, dataType = "String")
    })
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
    
     @RequestMapping(value = "/rulelists/{caseKey}",method=RequestMethod.GET)
     @ApiOperation(value="获取规则名称集合", notes="根据事件标识获取规则名称集合，用于按钮配置规则时获取当前事件下的所有规则")
     @ApiImplicitParam(name = "caseKey", value = "事件标识", required = true, dataType = "String")
     public List<Document> loadRuleLists(@PathVariable String caseKey) {
     	Document filter = new Document().append("caseKey", caseKey);
     	Document projection = new Document().append("_id", 1).append("ruleName", 1);
     	return ruleService.getRules(filter,projection);
     }
    
     @RequestMapping(value = "/rules",method=RequestMethod.POST)
     @ApiOperation(value="保存规则", notes="根据_id字段是否存在来判断当前是保存或修改")
     @ApiImplicitParam(name = "rule", value = "规则实体：{_id:'',caseKey:'',ruleCode:'',ruleName:'',drl:''}", required = true, dataType = "Document")
     public void addRules(@RequestBody Document rule) {
     	ruleService.saveRules(rule);
     }
     
      @RequestMapping(value = "/delrules",method=RequestMethod.POST)
      @ApiOperation(value="删除规则", notes="根据ID集合删除相应规则")
      @ApiImplicitParam(name = "ids", value = "规则ID集合", required = true, dataType = "List")
      public void delRules(@RequestBody List<String> ids) {
      	ruleService.deleteRuleById(ids);
      }
}
