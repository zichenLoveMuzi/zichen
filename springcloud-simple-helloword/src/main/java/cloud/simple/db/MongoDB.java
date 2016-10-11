package cloud.simple.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

/**
 * @brief
 * @details
 * @see
 * @author chunzhao.li
 * @version v1.0.0.0
 * @date 2016年9月28日下午4:33:48
 */
public class MongoDB {

	@Autowired
	private MongoDatabase database;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String COLLECTION_RULES = "lcmRules";

	/**
	 * @brief 插入文档(Document)到指定的集合(Collection)
	 * @param doc
	 *            需要被插入到集合的文档
	 * @param collectionName
	 *            集合名称
	 * @return void
	 * @throws @note
	 * @version v1.0.0.0
	 * @date 2016-5-24上午10:35:46
	 */
	public void insert(Document doc, String collectionName) {
		MongoCollection<Document> coll = database.getCollection(collectionName);
		long currentTime = System.currentTimeMillis();
		doc.append("createTime", currentTime).append("formatCreateTime", sdf.format(currentTime))
				.append("updateTime", currentTime).append("formatUpdateTime", sdf.format(currentTime));
		coll.insertOne(doc);
	}

	/**
	 * @brief 插入多个文档(Document)到指定的集合(Collection)
	 * @param docs
	 *            需要被插入到集合的文档列表
	 * @param collectionName
	 *            集合名称
	 * @return void
	 * @throws @note
	 * @version v1.0.0.0
	 */
	public void insertMany(List<Document> docs, String collectionName) {
		MongoCollection<Document> coll = database.getCollection(collectionName);
		if (docs != null && docs.size() > 0) {
			long currentTime = System.currentTimeMillis();
			for (Document doc : docs) {
				doc.append("createTime", currentTime).append("formatCreateTime", sdf.format(currentTime))
						.append("updateTime", currentTime).append("formatUpdateTime", sdf.format(currentTime));
			}
		}
		coll.insertMany(docs);
	}

	/**
	 * @brief 删除指定集合中符合条件的文档
	 * @param filter
	 *            文档过滤器
	 * @param collectionName
	 *            集合名称
	 * @return
	 * @throws @note
	 * @version v1.0.0.0
	 */
	public void delete(Bson filter, String collectionName) {
		MongoCollection<Document> coll = database.getCollection(collectionName);
		coll.deleteMany(filter);
	}

	/**
	 * @brief 查询指定集合中符合条件的文档
	 * @param filter
	 *            文档过滤器
	 * @param collectionName
	 *            集合名称
	 * @param sort
	 *            排序字段列表
	 * @return 文档集合
	 * @throws @note
	 * @version v1.0.0.0
	 */
	public List<Document> find(Bson filter, String collectionName, Bson sort, Bson projection) {
		List<Document> docs = new ArrayList<Document>();
		MongoCollection<Document> coll = database.getCollection(collectionName);
		FindIterable<Document> fi = null;
		if (filter != null) {
			fi = coll.find(filter);
		} else {
			fi = coll.find();
		}

		if (sort != null) {
			fi = fi.sort(sort);
		}

		if (projection != null) {
			fi.projection(projection);
		}

		if (fi != null) {
			MongoCursor<Document> cursor = fi.iterator();
			while (cursor.hasNext()) {
				docs.add(cursor.next());
			}
			cursor.close();
		}
		return docs;
	}

	/**
	 * @brief 查询指定集合中符合条件的第一个文档
	 * @param filter
	 *            文档过滤器
	 * @param collectionName
	 *            集合名称
	 * @return 一个文档
	 * @throws @note
	 * @version v1.0.0.0
	 */
	public Document findOne(Bson filter, String collectionName) {
		Document doc = null;
		List<Document> list = find(filter, collectionName, null, null);
		if (list != null && list.size() > 0) {
			doc = list.get(0);
		}
		return doc;
	}

	/**
	 * @brief 替换指定集合中符合条件的文档
	 * @param filter
	 *            文档过滤器
	 * @param replaceDoc
	 *            新文档，用于替换满足条件的文档
	 * @param collectionName
	 *            集合名称
	 * @return void
	 * @throws @note
	 * @version v1.0.0.0
	 */
	public void replace(Bson filter, Document replaceDoc, String collectionName) {
		MongoCollection<Document> coll = database.getCollection(collectionName);
		long currentTime = System.currentTimeMillis();
		replaceDoc.put("updateTime", currentTime);
		replaceDoc.put("formatUpdateTime", sdf.format(currentTime));
		coll.findOneAndReplace(filter, replaceDoc);
	}

	/**
	 * @brief 更新指定集合中符合条件的文档
	 * @param filter
	 *            文档过滤器
	 * @param update
	 *            需要更新的键值
	 * @param collectionName
	 *            集合名称
	 * @return void
	 * @throws @note
	 * @version v1.0.0.0
	 */
	public void update(Bson filter, Document update, String collectionName) {
		long currentTime = System.currentTimeMillis();
		update.put("updateTime", currentTime);
		update.put("formatUpdateTime", sdf.format(currentTime));
		MongoCollection<Document> coll = database.getCollection(collectionName);
		coll.updateMany(filter, eq("$set", update));
	}

	/**
	 * @brief 聚合查询
	 * @param pipeline
	 *            聚合管道，一系列构建的集合
	 * @param collectionName
	 *            集合名称
	 * @return 文档列表
	 * @throws @note
	 * @version v1.0.0.0
	 */
	public List<Document> aggregate(List<Bson> pipeline, String collectionName) {
		List<Document> docs = new ArrayList<Document>();
		MongoCollection<Document> coll = database.getCollection(collectionName);
		AggregateIterable<Document> ai = coll.aggregate(pipeline);
		MongoCursor<Document> cursor = ai.iterator();
		while (cursor.hasNext()) {
			docs.add(cursor.next());
		}
		cursor.close();
		return docs;
	}
}
