package cloud.simple.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.simple.db.MongoDB;

/**
 * @brief
 * @details
 * @see
 * @author chunzhao.li
 * @version v1.0.0.0
 * @date 2016年9月28日下午1:44:07
 */

@Service
public class RuleService {

	@Autowired
	MongoDB mongoDB;

	/**
	* @url 
	* @brief 保存规则到mongo （新增 or 修改）
	* @param Document 
	* @return 
	* @throws 
	* @note 
	* @author chunzhao.li
	* @version v1.0.0.0
	* @date 2016年10月10日上午10:10:16
	*/
	public void saveRules(Document map) {
		if (map.get("_id") != null) {
			Document filter = new Document().append("_id", new ObjectId(map.get("_id").toString()));
			map.remove("_id");
			mongoDB.update(filter, map, MongoDB.COLLECTION_RULES);
		}else{
			mongoDB.insert(map, MongoDB.COLLECTION_RULES);
		}
		
	}

	/**
	* @url 
	* @brief 根据参数获取相应规则
	* @param 
	* @return 
	* @throws 
	* @note 
	* @author chunzhao.li
	* @version v1.0.0.0
	* @date 2016年10月10日上午10:18:26
	*/
	public List<Document> getRules(Document filter,Document projection) {
		if (filter.get("_id") != null) {
			filter.put("_id", new ObjectId(filter.get("_id").toString()));
		}
		List<Document> result = mongoDB.find(filter, MongoDB.COLLECTION_RULES, new Document().append("_id",1), projection);
		if (result != null && result.size() > 0) {
			for (Document doc : result) {
				if (doc.get("_id") != null) {
					doc.put("_id", doc.get("_id").toString());
				}
			}
		}
		return result;
	}
	
	/**
	* @url 
	* @brief 根据ID删除规则
	* @param 
	* @return 
	* @throws 
	* @note 
	* @author chunzhao.li
	* @version v1.0.0.0
	* @date 2016年10月10日上午11:57:55
	*/
	public void deleteRuleById(List<String> ids){
		if(ids!=null && !ids.isEmpty()){
			for(String id : ids){
				mongoDB.delete(new Document().append("_id", new ObjectId(id)), MongoDB.COLLECTION_RULES);
			}
		}
	}

	/*
	 * new StringBuilder() .append("package com.sample ")
	 * .append("import java.util.HashMap; ")
	 * .append("dialect \"mvel\" ") .append("rule \"demo\" ")
	 * .append("when ")
	 * .append("$m : HashMap(inputButtonId == 'btn1' && (inputRoleId == '1' || inputRoleId == '2') && businessData.name == 'demo' && businessData.count > 5) "
	 * ) .append("then ").append("$m.outputRoleId = 2; ").
	 * append("$m.outputNextTaskId = 'taskN'; ")
	 * .append("end").toString();
	 */
	
	public Map<String, Object> getRuleResult(Map<String, Object> message) {
		if (message.get("ruleId") != null) {
			String ruleId = message.get("ruleId").toString();
			Map<String, Object> output = new HashMap<String, Object>();
			output.put("ruleId", ruleId);
			String drl = "";
			Document document = mongoDB.findOne(new Document().append("_id", new ObjectId(ruleId)),MongoDB.COLLECTION_RULES);
			if(document != null && document.getString("drl") != null && !document.getString("drl").isEmpty()){
				drl = document.getString("drl");
			}else{
				return output;
			}
			System.out.println("================================规则打印======================================");
	    	System.out.println(drl);
			StatefulKnowledgeSession kSession = null;
			try {
				KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
				// 装入规则，可以装入多个
				kb.add(ResourceFactory.newByteArrayResource(drl.getBytes("utf-8")), ResourceType.DRL);
				// kb.add(ResourceFactory.newByteArrayResource(rule2.getBytes("utf-8")),
				// ResourceType.DRL);

				KnowledgeBuilderErrors errors = kb.getErrors();
				for (KnowledgeBuilderError error : errors) {
					System.out.println(error);
				}
				KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
				kBase.addKnowledgePackages(kb.getKnowledgePackages());

				kSession = kBase.newStatefulKnowledgeSession();
				kSession.insert(message);
				kSession.fireAllRules();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				if (kSession != null)
					kSession.dispose();
			}

//			output.put("outputNextTaskId", message.get("outputNextTaskId"));
//			output.put("outputRoleId", message.get("outputRoleId"));
//			output.put("outputResult", message.get("outputResult"));
			output.putAll(message);
			//清空输出数据，todo... Tips：这里以后要优化一下，输出使用新的Map去操作，不要污染输入Map edit by chunzhao.li 2016.10.19
			message.remove("outputNextTaskId");
			message.remove("outputRoleId");
			message.remove("outputResult");
			return output;
		}else{
			return null;
		}
	}
}
