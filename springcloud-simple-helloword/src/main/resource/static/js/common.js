//通过服务器接口获取各项目基础地址-------------------------------------------

/**
 * @see 获取项目base路径（http://www.xxx.sss/cmcp）
 * @return  项目base路径
 */
function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPaht = curWwwPath.substring(0, pos);
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return(localhostPaht + projectName);
    return curWwwPath;
}

var baseUrl = getRootPath();

var storage = window.localStorage;


/**
 * @see 获取url参数 如http://localhost:8080/cmcp/a?username=sss
 * @param ?后面的key，如上面的username
 * @return  如果存在该key返回对应值，否则直接返回null
 */
function getURLParam(ParamName, parentSearch)
{
    var reg = new RegExp("(^|&)" + ParamName + "=([^&]*)(&|$)");
    var r = null;
    if (parentSearch) {
        r = decodeURI(parent.window.location.search).substr(1).match(reg);
    } else {
        r = decodeURI(window.location.search).substr(1).match(reg);
    }
    if (r != null) {
    	if(r[2]!='undefined'){
    		return unescape(r[2]);
    	}else{
    		return null;
    	}
    }
    return null;
}
(function($) {

    $.extend({
        /**
         * @see 将javascript数据类型转换为json字符串
         * @param 待转换对象,支持  object,array,string,function,number,boolean,regexp
         * @return 返回json字符串
         */
        toJSON: function(object) {
            return JSON.stringify(object);
        }
    });
})(jQuery)

/**
 * @see 使用ajax请求后台调用方法
 * @param  url 对应后台controller中的mapping
 * 				   data 对应后台controller中的方法参数
 * 				   callback 回调函数，会给回调函数传入一个对象如为data，
 * 								如果data.result为true的话则说明响应成功，否则为相应失败
 * 								其中data.data为后台返回的数据
 * 				   conf 配置对象，其中async：是否异步
 * 									  traditional:是否不进行深度序列化，默认为false(是)
 * 									  modal：是否显示进度条
 * @return  如果存在该key返回对应值，否则直接返回null
 */
function doJsonRequest(url, data, callBack, conf, method) {
	var url = baseUrl + url;
	var tempasync = true;
    var traditional = false;
	if (conf) {
        if (conf.async != "undefined") {
            tempasync = conf.async;
        }
        if(conf.traditional !="undefined"){
        	traditional = conf.traditional;
        }
        if(conf.modal !="undefined" && !conf.modal){
        }
    }
    if(method==null||method==""){
    	method = "POST";
    }else if("GET" == method){
    	if(url.indexOf("?")>-1){
    		url+="&random="+Math.random();
    	}else{
    		url+="?random="+Math.random();
    	}
    	
    }
    var jdata = null;
    if(data!=null||data!=""){
    	jdata = JSON.stringify(data)
    }
    
    $.ajax({
        type: method,
        url: url,
        timeout: 600000,
        data: jdata,
        async: tempasync,
        traditional: traditional,
        dataType: "json",
        contentType: "application/json",
        success: function(data, textStatus, XMLHttpRequest) {
            var obj = {};
            obj.result = true;
            if (textStatus != "success") {
                obj.result = false;
            }
            obj.data = data;
            callBack(obj, textStatus, XMLHttpRequest);
            
        }
    });
}