/**
 *  BaseComponent 代码书写按这个类的规定的方法写
 *      this.model 存数据 可监听数据变化
 *      this.dataLoader 页面初始化要存储的数据
 *      this.init dom加载完初始化执行
 *      this.uiEvents ui事件写在这里
 *      this.watchModel model监控写在这里
 *  BaseComponent.template 模板引擎
 *  BaseComponent.action 事件绑定
 *  BaseComponent.derive 派生对象
 *  @author Lin jing ming(linjm@landicorp.com)
 *
 **/
layui.define(['tool'], function (exports) {
    /**
     *  Model数据模型
     */
    var Model = function() {
        this.store = {};
        this.promiseList = {};
        this.watchList = {};
    };


    Model.prototype.get = function(key) {
        var list = this.store;
        return list[key];

    };

    Model.prototype.loaded = function(key) {
        var list = this.promiseList;
        if (list[key]) {
            return list[key];
        }

    };

    Model.prototype.set = function(key, value) {
        var self = this;
        if (!key) return;
        var oldValue = this.store[key];
        if (!layui.tool.eq(oldValue, value)) {
            setTimeout(function() {
                self.trigger(key, oldValue);
            }, 0);
        }
        this.store[key] = value;
    };

    Model.prototype.fill = function(obj) {
        if (Object.prototype.toString.call(obj) === '[object Object]') {
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    this.set(key, obj[key]);
                }
            }
        }
    };

    Model.prototype.watch = function(name, handler) {
        var self = this;
        var callback = handler || function() {
        };
        self.watchList[name] = self.watchList[name] || [];
        self.watchList[name].push(callback);
    };

    Model.prototype.trigger = function(name, param) {
        var len = this.watchList[name] && this.watchList[name].length || 0;
        for (var i = 0; i < len; i++) {
            this.watchList[name][i](this.store[name], param);
        }
    };

    var BaseComponent = function() {
        if (!(this instanceof BaseComponent)) return new BaseComponent();
        var self = this;
        this.model = new Model();
        setTimeout(function () {
            self.domReady();
        },10);
    };

    BaseComponent.prototype.domReady = function() {
        var self = this;
        var uiEvents = self.uiEvents && self.uiEvents();
        self.init && self.init();
        self.watchModel && self.watchModel();
        if (Object.prototype.toString.call(uiEvents) === '[object Object]') {
            $('#' + self.modeName).length ?
                BaseComponent.action().listen(uiEvents, $('#' + self.modeName))
                :
                BaseComponent.action().listen(uiEvents);
        }
    };

    BaseComponent.prototype.init = function() {

    };

    BaseComponent.prototype.dataLoader = function(obj) {
        var self = this;
        var temp;
        for (var key in obj) {
            if (!obj.hasOwnProperty(key)) return;
            if ($.isFunction(obj[key])) {
                temp = obj[key]();
            } else {
                temp = obj[key];
            }
            if (temp.promise) {
                self.model.promiseList[key] = temp;
                (function(key) {
                    temp.then(function(res) {
                        self.model.store[key] = res;
                    })
                })(key);
            } else {
                self.model.store[key] = temp;
                self.model.promiseList[key] = $.Deferred(function(def) {
                    def.resolve(temp);
                }).promise();
            }
        }
    };

    BaseComponent.prototype.uiEvents = function() {

    };

    BaseComponent.prototype.watchModel = function() {

    };

    /**
     *  继承
     *  @param obj {object} 要继承的baseCompoent对象
     *  @param isModelShare {boolean} 是否共享model
     */
    BaseComponent.derive = function(obj, isModelShare) {
        function F() {
            var self = this;
            if (!isModelShare && isModelShare === false) {
                var pmodel = this.model;
                this.model = new Model();
                this.model.store = $.extend(true, {}, pmodel.store);
                this.model.promiseList = $.extend(true, {}, pmodel.promiseList);
            }
            $(function() {
                self.domReady();
            })
        }

        F.prototype = obj;
        return new F();
    };
    // 事件监听
    BaseComponent.action = function() {
        var cache = {
            dom: [],
            eventType: [],
            action: []
        };

        var actionKeyVal = 'data-action';
        var r = {
            setActionKey: function(key) {
                actionKeyVal = key;
                r.setActionKey = null;
            },
            /**
             * 利用冒泡来做监听，这样做有以下优势：
             *     1. 减少事件绑定数量，提高程序效率，尤其在列表性质的节点上，无需每个节点都绑定
             *     2. 动态生成的内容无需绑定事件也能响应
             *     3. 节点外点击隐藏某些节点
             * @param actions {Object} 响应类型与对应处理事件，
             *     键：是事件源上的data-action属性
             *     值：可能是下面两种格式：
             *         function：回调函数，当事件源触发时候执行该函数，函数的this是事件源的jQuery节点，参数是事件对象
             *         object：对象，包含两个属性：
             *             is：回调函数，事件源触发时候执行，this是事件源的jQuery节点，参数是事件对象
             *             not：点击在其他节点上时执行的回调函数，this是事件源的jQuery节点，参数是事件对象,
             *             callback： 同is
             *             scope：改变callback中的this
             * @param node {Object} jQuery对象，绑定的节点，是父容器
             * @param type 事件类型，默认是click，基本也都是处理click事件
             * @return {object} jQuery对象，父节点
             */
            listen: function(actions, node, type) {
                actions = actions || {};
                node = node ? $(node) : $(document);
                type = type || 'click';

                var index = $.inArray(node[0], cache.dom);
                if (index !== -1) {
                    if (cache.eventType[index] !== type) {
                        index = -1;
                    }
                }

                if (index === -1) {
                    cache.dom.push(node[0]);
                    cache.eventType.push(type);
                    cache.action.push(actions);
                    index = cache.action.length - 1;

                    node.off(type + '.' + actionKeyVal).on(type + '.' + actionKeyVal, function(e) {
                        var target = $(e.target);
                        var actionKey = target.attr(actionKeyVal);
                        var xnode = target;
                        if (!actionKey) {
                            xnode = target.closest('[' + actionKeyVal + ']');
                            actionKey = xnode.attr(actionKeyVal);
                        }
                        var flag = true;
                        var fetch = cache.action[index];
                        var fetchAction = fetch[actionKey];
                        if (fetchAction) {
                            if ($.isFunction(fetchAction)) {
                                flag = fetchAction.call(target, e, xnode, actionKey);
                            } else {
                                if ($.isFunction(fetchAction.is) || $.isFunction(fetchAction.action)) {
                                    var fn = fetchAction.is || fetchAction.action;
                                    flag = fn.call(fetchAction.scope || target, e, xnode, actionKey);
                                }
                            }
                        }
                        for (var i = 0, len = cache.action.length; i < len; i++) {
                            var temp = cache.action[i];
                            for (var key in temp) {
                                if (key !== actionKey && temp[key] && temp[key].not && $.isFunction(temp[key].not)) {
                                    temp[key].not.call(temp[key].scope || target, e, node.find('[' + actionKeyVal + '=' + key + ']'), actionKey);
                                }
                            }
                        }
                        if (flag === -1) { // 禁用冒泡
                            e.stopPropagation();
                        } else if (flag === true) { // 都不禁用
                            return true;
                        } else if (flag === false) { // 都禁用
                            return false;
                        } else { // 默认无返回值时禁用默认行为，但不禁用冒泡
                            e.preventDefault();
                        }
                    });
                } else {
                    var fetch = cache.action[index];
                    for (var key in actions) {
                        fetch[key] = actions[key];
                    }
                }
                return node;
            }
        };
        return r;
    };

    //暴露接口
    exports('baseComponent', BaseComponent);
});