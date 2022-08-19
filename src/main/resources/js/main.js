(function(doc, win) {
    var docEl = doc.documentElement,
        resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
        recalc = function() {
            var clientWidth = docEl.clientWidth;
            if (!clientWidth) return;
            docEl.style.fontSize = 100 * (clientWidth / 750) + 'px';
        };
	recalc();
    if (!doc.addEventListener) return;
    win.addEventListener(resizeEvt, recalc, false);
    doc.addEventListener('DOMContentLoaded', recalc, false);
})(document, window);


$(document).ready(function() {
    // 禁止安卓客户调整字体大小
	(function() {
	    if (typeof WeixinJSBridge == "object" && typeof WeixinJSBridge.invoke == "function") {
	        handleFontSize();
	    } else {
	        if (document.addEventListener) {
	            document.addEventListener("WeixinJSBridgeReady", handleFontSize, false);
	        } else if (document.attachEvent) {
	            document.attachEvent("WeixinJSBridgeReady", handleFontSize);
	            document.attachEvent("onWeixinJSBridgeReady", handleFontSize);
	        }
	    }

	    function handleFontSize() {
	        // 设置网页字体为默认大小
	        WeixinJSBridge.invoke('setFontSizeCallback', { 'fontSize' : 0 });
	        // 重写设置网页字体大小的事件
	        WeixinJSBridge.on('menu:setfont', function() {
	            WeixinJSBridge.invoke('setFontSizeCallback', { 'fontSize' : 0 });
	        });
	    }
	})(); 

	$('input,textarea').on('blur', function() {
	    var ua = navigator.userAgent.toLowerCase();
	    if(/micromessenger/.test(ua)) {
	        if(/iphone|ipad|ipod/.test(ua)) {
	            var currentPosition, timer;
	            var speed = 1; 
	            timer = setInterval(function() {
	                currentPosition=document.documentElement.scrollTop || document.body.scrollTop;
	                currentPosition-=speed;
	                window.scrollTo(0,currentPosition);
	                currentPosition+=speed; 
	                window.scrollTo(0,currentPosition);
	                clearInterval(timer);
	            }, 1);
	        }
	    }
	});

	function orient() {
		if (window.orientation == 90 || window.orientation == -90) {
			//ipad、iphone竖屏；Andriod横屏
			$("#lock").show();
		} else if (window.orientation == 0 || window.orientation == 180) {
			//ipad、iphone横屏；Andriod竖屏
			$("#lock").hide();
		}
	}
	//页面加载时调用
	$(function() {
		orient();
	});
	//用户变化屏幕方向时调用
	$(window).bind('orientationchange', function(e) {
		orient();
	});

	// 首页规则
	$('.show_btn').click(function(){
		var txt = $(this).text();
		if(txt != "收起"){
			$('.bt_rule_content').show();
			$(this).addClass("rotate").html('收起');
			$("body,html").scrollTop(1000000);
		}
		else{
			$('.bt_rule_content').hide();
			$(this).removeClass("rotate").html('展开');
		}
	});

	// 答题
	var sum = 0;
	$('.item p').click(function () {
		$(this).addClass('current').siblings('p').removeClass('current');
		if ($(this).attr('value') == 'true') {
			sum += 1
		} else {
			sum = sum - 1
		}
		console.log(sum)
	});

	$('.submit_btn').click(function () {
		$("body,html").scrollTop(0);
		var lg = $(".current").length;
		if (lg < 3) {
			$('.float_mask').fadeIn();
			setTimeout(function () {
				$('.float_mask').hide();
			}, 2000);
		}
		else {
			$('.correct_tips').show();

			$(".item").each(function () {

				if ($(this).find(".current").attr("value") != 'true') {
					$(this).find(".current").addClass('error');
					$(".item_wrapper h1").show().html('未全部答对，请重新答题');
					$('.submit_btn').addClass('again_btn').html('重新答题');
					$('.submit_btn').click(function () {
						window.location.reload();
					});
				}
				else {
					$(this).find(".current").addClass('correct');
					if (sum == 3) {
						$(".item p").unbind('click');
						$(".item_wrapper h1").show().html('恭喜通关，请至页面底部领券');
						$('.submit_btn').addClass('rec_btn').html('领取奖品');
						
						$('.submit_btn').click(function () {
							window.location.href="https://saclub.ecc.net.cn/demo/aipao202208/index.html";
						});
					}
            	}
			});
		}
	});

});