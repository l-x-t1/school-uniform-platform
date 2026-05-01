function TestProgress() {
    this.OnBegin = null;
    this.OnProgress = null;
    this.OnError = null;
    this.OnEnd = null;
    this.count = 0;
}

function TestFactory(testProgress, breakOnError) {
    this.testSuites = new Array();
    this.testProgress = testProgress;
    this.breakOnError = breakOnError;
    this._r_i = 0;
    this._r_j = 0;
    this._index = 0;
    this.successCount = 0;
    this.failCount = 0;
    this.count;
}

TestFactory.prototype.AddTestSuit = function(testSuit) {
    this.testSuites.push(testSuit);
}

TestFactory.prototype.Clean = function() {
    this.testSuites = new Array();
}

var currentTestFactory = null;
TestFactory.prototype.Run = function() {
    //get all count
    var allTestCount = 0;
    var currentIndex = 0;
    for (var i = 0; i < this.testSuites.length; i++) {
        var testSuit = this.testSuites[i];
        for (var j = 0; j < testSuit.testes.length; j++) {
            allTestCount++;
            testSuit.testes[j]._hasSuccess = false;
            testSuit.testes[j].passTime = 0;
        }
    }
    this.count = allTestCount;

    if (this.testProgress) {
        this.testProgress.OnBegin(allTestCount);
    }

    this._r_i = 0;
    this._r_j = 0;
    this._index = 0;
    this.successCount = 0;
    this.failCount = 0;
    currentTestFactory = this;

    setTimeout(this._RunTest, 100);

}

TestFactory.prototype.RefreshStat = function() {
    this.successCount = 0;
    this.failCount = 0;
    this.count = 0;

    for (var i = 0; i < this.testSuites.length; i++) {
        var testSuit = this.testSuites[i];
        for (var j = 0; j < testSuit.testes.length; j++) {
            this.count++;
            if(testSuit.testes[j]._hasSuccess)
                this.successCount++;
            else
                this.failCount++;
        }
    }
}

TestFactory.prototype.GetByIndex = function(index) {
    var c = index;
    for (var i = 0; i < this.testSuites.length; i++) {
        var testSuit = this.testSuites[i];
        if (c < testSuit.testes.length)
            return testSuit.testes[c];
        else
            c -= testSuit.testes.length;
    }
    return null;
}

TestFactory.prototype._RunTest = function() {
    var ctf = currentTestFactory;
    if (ctf._r_i >= ctf.testSuites.length) {
        if (ctf.testProgress) {
            ctf.testProgress.OnEnd();
        }
        return;
    }

    var testSuit = ctf.testSuites[ctf._r_i];

    if (ctf._r_j >= testSuit.testes.length) {
        ctf._r_i++;
        ctf._r_j = 0;
        setTimeout(ctf._RunTest, 100);
    }

    var test = testSuit.testes[ctf._r_j];

    if(test.asyn==undefined || test.asyn==false){
        try {
            var date1=new Date();
            test.fun();
            var date2=new Date();

            test.passTime = TimeUtil.SecondDif(date1, date2);

            if (ctf.testProgress) {
                ctf.testProgress.OnProgress(ctf._index);
            }

            ctf.successCount++;
            test._hasSuccess = true;
        }
        catch (e) {
            if (ctf.testProgress) {
                ctf.testProgress.OnError(ctf._index, e.message, ctf.breakOnError);
            }
            ctf.failCount++;
            test._hasSuccess = false;
            if (ctf.breakOnError)
                return;
        }

        ctf._index++;
        if (ctf._r_j < testSuit.testes.length - 1)
            ctf._r_j++;
        else {
            ctf._r_i++;
            ctf._r_j = 0;
        }

        setTimeout(ctf._RunTest, 100);
    }
    else{
        var date1=new Date();

        var testResult={
            done: function () {
                var date2=new Date();

                test.passTime = TimeUtil.SecondDif(date1, date2);

                if (ctf.testProgress) {
                    ctf.testProgress.OnProgress(ctf._index);
                }

                ctf.successCount++;
                test._hasSuccess = true;

                ctf._index++;
                if (ctf._r_j < testSuit.testes.length - 1)
                    ctf._r_j++;
                else {
                    ctf._r_i++;
                    ctf._r_j = 0;
                }

                setTimeout(ctf._RunTest, 100);
            },
            fail: function (message) {
                if (ctf.testProgress) {
                    ctf.testProgress.OnError(ctf._index, message, ctf.breakOnError);
                }
                ctf.failCount++;
                test._hasSuccess = false;
                if (ctf.breakOnError)
                    return;

                ctf._index++;
                if (ctf._r_j < testSuit.testes.length - 1)
                    ctf._r_j++;
                else {
                    ctf._r_i++;
                    ctf._r_j = 0;
                }

                setTimeout(ctf._RunTest, 100);
            }
        };

        try{
            test.fun(testResult);
        }
        catch (e) {
            testResult.fail(e.message);
        }
    }
}

var allTestes = new TestFactory(null, false);

//-----------------------------------------------------------
function TestSuit(name){
	this.name=name;
	this.testes = new Array();
	this.stepInfo = "";
	this.auto = true;
}

TestSuit.prototype.AddTest = function(test) {
    this.testes.push(test);
}

TestSuit.prototype.Clean = function() {
    this.testes = new Array();
}

TestSuit.prototype.DelayLoad = null;

//-----------------------------------------------------------

function Test(name,fun) {
    this.name = name;
    this.fun = fun;
    this._hasSuccess = false;
    this.passTime = 0;
    this.asyn=false;   //是否异步
}

//------------------------------------------------------------
var Assert = new Object();
Assert.Assert = function(condition, mess, msgPre) {
    if (!condition) {
        var message = "";
        if (msgPre)
            message += msgPre;
        message+="Assert failed: false ";
        if (mess)
            message += ",message:" + mess;

        throw new Error(message);
    }
}

Assert.AssertAsyn = function(testResult, condition, mess, msgPre) {
    if (!condition) {
        var message = "";
        if (msgPre)
            message += msgPre;
        message+="Assert failed: false ";
        if (mess)
            message += ",message:" + mess;

        testResult.fail(message);
    }
}

Assert.AssertEqual = function(exp, act, msgPre) {
    if (exp != act) {
        var caller = Assert.Assert.caller;
        var message = "";
        if (msgPre)
            message += msgPre;
        message+= "Assert failed: exp " + exp + ",act " + act;

        throw new Error(message);
    }
}

Assert.AssertEqualAsyn = function(testResult, exp, act, msgPre) {
    if (exp != act) {
        var caller = Assert.Assert.caller;
        var message = "";
        if (msgPre)
            message += msgPre;
        message+= "Assert failed: exp " + exp + ",act " + act;

        testResult.fail(message);
    }
}

Assert.AssertNoException = function(fun, msgPre) {
    try {
        fun();
    }
    catch (e) {
        var msg = e.message;
        if (msgPre)
            msg = msgPre + msg;


        throw new Error(msg);
    }
}

Assert.AssertNoExceptionAsyn = function(testResult,fun, msgPre) {
    try {
        fun();
    }
    catch (e) {
        var msg = e.message;
        if (msgPre)
            msg = msgPre + msg;

        testResult.fail(message);
    }
}

Assert.AssertException = function(fun, failMsg, exceptionMsg, msgPre) {

    try {
        fun();
    }
    catch (e) {
        if (exceptionMsg != null)
            Assert.AssertEqual(exceptionMsg, e.message, msgPre);
        return;
    }

    Assert.Assert(false, failMsg, msgPre);

}

Assert.AssertExceptionAsyn = function(testResult, fun, failMsg, exceptionMsg, msgPre) {

    try {
        fun();
    }
    catch (e) {
        if (exceptionMsg != null)
            Assert.AssertEqualAsyn(testResult, exceptionMsg, e.message, msgPre);
        return;
    }

    Assert.AssertAsyn(testResult, false, failMsg, msgPre);

}


var Trace = new Object();
Trace.Trace = function(msg) {
    trace(msg);
}
Trace.TraceLn = function(msg) {
    trace(msg+"<br>");
}
Trace.Info = function(msg) {
    trace(msg + "<br>");
}
Trace.Warning = function(msg) {
    trace("<span style='color:#A6A600'> 警告："+msg + "</span><br>");
}
Trace.Error = function(msg) {
    trace("<span style='color:red'> 错误：" + msg + "</span><br>");
}

var TimeUtil = new Object();
TimeUtil.NowDateStr = function() {
    var now = new Date();
    return now.getFullYear() + "-" + now.getMonth() + "-" + now.getDay() + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
}

TimeUtil.NowTimeStr = function() {
    var now = new Date();
    return now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
}

TimeUtil.SecondDif = function(date1, date2) {
    var m1 = date1.getHours() * 60 * 60 * 1000 + date1.getMinutes() * 60 * 1000 + date1.getSeconds() * 1000 + date1.getMilliseconds();
    var m2 = date2.getHours() * 60 * 60 * 1000 + date2.getMinutes() * 60 * 1000 + date2.getSeconds() * 1000 + date2.getMilliseconds();
    return (m2 - m1) / 1000;
}
