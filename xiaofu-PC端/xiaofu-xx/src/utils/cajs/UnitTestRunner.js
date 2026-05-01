
var progress = new TestProgress();
var tf = null;
var testListColnum = 6;

progress.OnBegin = function(count) {
    var p = document.getElementById("progress");
    p.style.width = "0px";
    p.style.backgroundColor = "green";
    this.count = count;

    UpdateTestStat(tf.count, tf.successCount, tf.failCount);
}

progress.OnProgress = function(index) {
    var p = document.getElementById("progress");
    var pb = document.getElementById("progressBorder");
    p.style.width = (parseInt(pb.style.width) * index) / (this.count) + "px";

    var output = document.getElementById("output");
    var doc = output.contentWindow.document;
    var tr = doc.getElementById("tr_" + index);
    tr.innerHTML = "成功";
    var ttime = doc.getElementById("ttime_" + index);
    ttime.innerHTML = tf.GetByIndex(index).passTime;

    var ttable = doc.getElementById("ttable");
    var scrollHeight = ttable.scrollTop;
    for (var i = 0; i <= index; i++)
        scrollHeight += ttable.rows[index].scrollHeight;
    output.contentWindow.scrollTo(0, scrollHeight-400);

    UpdateTestStat(tf.count, tf.successCount, tf.failCount);

}

progress.OnError = function(index, message, breakOnError) {
    var p = document.getElementById("progress");
    p.style.backgroundColor = "red";

    var output = document.getElementById("output");
    var doc = output.contentWindow.document;

    var tr = doc.getElementById("tr_" + index);
    if(tr)
    {
        tr.innerHTML = "<span style='color:red'>失败</span>";
    }
    var ti = doc.getElementById("ti_" + index);
    if(ti)
    {
        ti.innerHTML = message;
    }
    var ttime = doc.getElementById("ttime_" + index);
    if(ttime){
        ttime.innerHTML = tf.GetByIndex(index).passTime;
    }

    UpdateTestStat(tf.count, tf.successCount, tf.failCount);
}

progress.OnEnd = function() {
    var p = document.getElementById("progress");
    var pb = document.getElementById("progressBorder");
    p.style.width = pb.style.width;

    UpdateTestStat(tf.count, tf.successCount, tf.failCount);
}

function UpdateTestStat(all,success,fail) {
    var s = document.getElementById("testStat");
    s.innerHTML = "共" + all + "个测试，通过" + success + "个，失败" + fail + "个";
}

window.onload = function() {

    var div = document.getElementById("allTestesDiv");
    var s = "";
    for (var i = 0; i < allTestes.testSuites.length; i++) {

        s += "<input type='checkbox' name='testes' id='testes_" + i + "'/><span style='padding-right: 10px;padding-left: 5px'>" + allTestes.testSuites[i].name + "</span>";
        if (allTestes.testSuites[i].stepInfo != "")
            s += "<a href='#' onclick='ViewStepInfo(" + i + ")'>步骤</a>";

    }

    div.innerHTML = s;
}

function Select(s,type) {
    for (var i = 0; i < allTestes.testSuites.length; i++) {
        var auto = allTestes.testSuites[i].auto;
        if (type == 0) {
            var cb = document.getElementById("testes_" + i);
            cb.checked = s;
        } else if (type == 1) {
             if (auto) {
                 var cb = document.getElementById("testes_" + i);
                 cb.checked = s;
            }
        }
        else if (type == 2) {
            if (!auto) {
                var cb = document.getElementById("testes_" + i);
                cb.checked = s;
            }
        }



    }
}

function ViewStepInfo(index) {
    alert(allTestes.testSuites[index].stepInfo);
}

function ViewCode(index) {
    alert(tf.GetByIndex(index).fun.toString());
}

function ReDo(index) {
    var output = document.getElementById("output");
    var doc = output.contentWindow.document;
    var tr = doc.getElementById("tr_" + index);
    tr.innerHTML = "等待测试...";
    var test = tf.GetByIndex(index);

    try {
        var date1 = new Date();
        if(test.asyn==undefined || test.asyn==false){
        	test.fun();
            var date2 = new Date();

            test.passTime = TimeUtil.SecondDif(date1, date2);
            test._hasSuccess = true;

            var tr = doc.getElementById("tr_" + index);
            tr.innerHTML = "成功";
            var ti = doc.getElementById("ti_" + index);
            ti.innerHTML = "";
        }
        else{
        	 var testResult={
	            done: function () {
	                var date2 = new Date();

	                test.passTime = TimeUtil.SecondDif(date1, date2);
	                test._hasSuccess = true;

	                var tr = doc.getElementById("tr_" + index);
	                tr.innerHTML = "成功";
	                var ti = doc.getElementById("ti_" + index);
	                ti.innerHTML = "";
	            },
	            fail: function (message) {
	                test._hasSuccess = false;
	                var tr = doc.getElementById("tr_" + index);
	                tr.innerHTML = "<span style='color:red'>失败</span>";
	                var ti = doc.getElementById("ti_" + index);
	                ti.innerHTML = message;
	            }
	        };
        	 test.fun(testResult);
        }

    }
    catch (e) {
        test._hasSuccess = false;
        var tr = doc.getElementById("tr_" + index);
        tr.innerHTML = "<span style='color:red'>失败</span>";
        var ti = doc.getElementById("ti_" + index);
        ti.innerHTML = e.message;
    }

    var ttime = doc.getElementById("ttime_" + index);
    ttime.innerHTML = tf.GetByIndex(index).passTime;

    tf.RefreshStat();
    UpdateTestStat(tf.count, tf.successCount, tf.failCount);
    if (tf.failCount > 0) {
        var p = document.getElementById("progress");
        p.style.backgroundColor = "red";
    }
    else {
        var p = document.getElementById("progress");
        p.style.backgroundColor = "green";
    }

}

function doTest() {

    var output = document.getElementById("output");
    var doc = output.contentWindow.document;
    doc.body.innerHTML = "";

    tf = new TestFactory(progress, false);

    //加载 晚加载测试
    for (var i = 0; i < allTestes.testSuites.length; i++) {
        if (allTestes.testSuites[i].DelayLoad)
            allTestes.testSuites[i].DelayLoad();
    }

    doc.write("<table style='width:100%; border: solid 1px' id='ttable'>");
    doc.write("<tr style='background-color:#cccccc'><td>测试组</td><td>测试名称</td><td>结果</td><td>用时</td><td>信息</td><td>操作</td></tr>");

    var index = 0;
    for (var i = 0; i < allTestes.testSuites.length; i++) {
        var id = "testes_" + i;
        var t = document.getElementById(id);
        if (t.checked) {
            var test = allTestes.testSuites[i];
            tf.AddTestSuit(test);

            for (var j = 0; j < test.testes.length; j++) {
                doc.write("<tr>");
                doc.write("<td>" + test.name + "</td>");

                var t1 = test.testes[j];
                if (t1.name)
                    doc.write("<td>" + t1.name + "</td>");
                else
                    doc.write("<td>未命名测试</td>");

                doc.write("<td><span id='tr_" + index + "'>等待测试...</span></td>");
                doc.write("<td><span id='ttime_" + index + "'></span></td>");
                doc.write("<td><span id='ti_" + index + "'></span></td>");

                doc.write("<td>");
                doc.write("<a onclick='parent.ReDo(" + index + ")' href='#'>重新执行</a>&nbsp;");
                doc.write("<a onclick='parent.ViewCode(" + index + ")' href='#'>查看代码</a>&nbsp;");
                doc.write("</td>");
                doc.write("</tr>");
                index++;
            }
        }
    }

    doc.write("</table>");

    tf.Run();
}

function trace(msg) {
    try
    {
        var output = document.getElementById("trace");
        var doc = output.contentWindow.document;
        doc.write(msg);
        output.contentWindow.scrollTo(0, doc.body.scrollHeight);
    }
    catch(e) {
    }

}

function cleanInfo() {
    var output = document.getElementById("trace");
    var doc = output.contentWindow.document;
    doc.body.innerHTML = "";
}
