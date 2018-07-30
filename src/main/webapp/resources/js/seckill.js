// 存放主要交互逻辑js代码
// javascript 模块化
var seckill = {
    // 封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        }
        , exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        }
        , execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    }
    , handleSeckill: function (seckillId, node) {

        // 获取秒杀地址，控制实现逻辑，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {

            // 在回调函数中，执行交互流程
            if (result && result['success']) {

                let exposer = result['data'];
                if (exposer['exposed']) {
                    // 开启秒杀 获取秒杀地址
                    var md5 = exposer['md5'];

                    // 绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        // 绑定执行秒杀请
                        $(this).addClass('disabled');   // 1）先禁用按钮
                        $.post(seckill.URL.execution(seckillId, md5), {}, function (result) {
                            console.log(result);
                            console.log(result['success']);
                            if (result && result['success']) {
                                console.log('miaoshachengong');
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                // 显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>')
                            }
                        });
                    });
                    node.show();

                } else {
                    // 未开启秒杀
                    let now = exposer['now'];
                    let start = exposer['start'];
                    let end = exposer['end'];
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log('result:' + result.toString());
            }
        });
    }
    , validatePhone: function (phone) {
        return phone && phone.length == 11 && !isNaN(phone);
    }
    // 时间判断
    , countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        console.log(seckillId, nowTime, startTime, endTime);
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束！');
        } else if (nowTime < startTime) {
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {

                // 获取秒杀地址，控制实现逻辑，执行秒杀
                seckill.handleSeckill(seckillId, seckillBox);
            });
        } else {

            // 秒杀开始
            seckill.handleSeckill(seckillId, seckillBox);
        }
    }
    , detail: {
        // 详情页初始化
        init: function (params) {
            // 用户手机验证和登录，计时交互
            // 规划交互流程
            // 在cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            // 验证手机号
            if (!seckill.validatePhone(killPhone)) {
                // 绑定phone 控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,
                    backdrop: 'static', // 禁止位置关闭
                    keyboard: false
                });

                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('inputPhone=' + inputPhone); // TODO
                    if (seckill.validatePhone(inputPhone)) {
                        // 刷新页面
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>>').show(300);

                    }

                });
            }

            // 已经登录
            // 计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];

                    // 时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                }
                else {
                    console.log('result:' + result.toString());
                }
            });
        }
    }
};