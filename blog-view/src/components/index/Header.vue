<template>
	<header ref="header">
		<div class="view">
      <!-- 在这里修改首页背景图片 -->
<!--			<img ref="imgbg1" src="https://cdn.jsdelivr.net/gh/Naccl/blog-resource/img/bg1.jpg" style="display: none;">-->
<!--			<div class="bg1" style="background-image: url('https://cdn.jsdelivr.net/gh/Naccl/blog-resource/img/bg1.jpg');"></div>-->
<!--			<div class="bg2" style="background-image: url('https://cdn.jsdelivr.net/gh/Naccl/blog-resource/img/bg2.jpg');"></div>-->
<!--			<div class="bg3" style="background-image: url('https://cdn.jsdelivr.net/gh/Naccl/blog-resource/img/bg3.jpg');" v-show="loaded"></div>-->
      <img ref="imgbg1" src="http://101.34.137.166:8055/common/upload/download?name=fd23a292-92cc-4642-be4d-294768d215971735524816721_IMG_20241230_101328.png&dir=很烦" style="display: none;">
      <div class="bg1" style="background-image: url('http://101.34.137.166:8055/common/upload/download?name=fd23a292-92cc-4642-be4d-294768d215971735524816721_IMG_20241230_101328.png&dir=很烦');"></div>
			<div class="bg2" style="background-image: url('http://101.34.137.166:8055/common/upload/download?name=3a2d0dcc-0d63-4f06-b5ba-a743fdde39d01735524428549_Image_240088055610494.png&dir=很烦');"></div>
			<div class="bg3" style="background-image: url('http://101.34.137.166:8055/common/upload/download?name=4d108ec8-fc7c-4f2e-ade6-30f9a261b38a1735524427851_mmexport1735390638846.png&dir=%E5%BE%88%E7%83%A6');" v-show="loaded"></div>
		</div>
    <!-- 首页标题 -->
		<div class="text-malfunction" data-word="FantasyGames">
			<div class="line"></div>
		</div>
		<div class="wrapper">
			<i class="ali-iconfont icon-down" @click="scrollToMain"></i>
		</div>
		<div class="wave1"></div>
		<div class="wave2"></div>
	</header>
</template>

<script>
	import {mapState} from 'vuex'

	export default {
		name: "Header",
		data() {
			return {
				loaded: false
			}
		},
		computed: {
			...mapState(['clientSize'])
		},
		watch: {
			'clientSize.clientHeight'() {
				this.setHeaderHeight()
			}
		},
		mounted() {
			/**
			 * 因为bg3.jpg比较小，通常会比bg1.jpg先加载，显示出来会有一瞬间bg1显示一半，bg3显示一半，为了解决这个问题，增加这个判断，让bg1加载完毕后再显示bg3
			 * HTML中使用img标签的原因：我个人想用div作为图片的载体，而只有img标签有图片加载完毕的onload回调，所以用一个display: none的img人柱力来加载图片
			 * 当img中的src加载完毕后，会把图片缓存到浏览器，后续在div中用background url的形式将直接从浏览器中取出图片，不会下载两次图片
			 */
			this.$refs.imgbg1.onload = () => {
				this.loaded = true
			}
			this.setHeaderHeight()
			let startingPoint
			const header = this.$refs.header
			header.addEventListener('mouseenter', (e) => {
				startingPoint = e.clientX
			})
			header.addEventListener('mouseout', (e) => {
				header.classList.remove('moving')
				header.style.setProperty('--percentage', 0.5)
			})
			header.addEventListener('mousemove', (e) => {
				let percentage = (e.clientX - startingPoint) / window.outerWidth + 0.5
				header.style.setProperty('--percentage', percentage)
				header.classList.add('moving')
			})
		},
		methods: {
			//根据可视窗口高度，动态改变首图大小
			setHeaderHeight() {
				this.$refs.header.style.height = this.clientSize.clientHeight + 'px'
			},
			//平滑滚动至正文部分
			scrollToMain() {
				window.scrollTo({top: this.clientSize.clientHeight, behavior: 'smooth'})
			}
		},
	}
</script>

<style scoped>
	header {
		--percentage: 0.5;
		user-select: none;
	}

	.view {
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		display: flex;
		justify-content: center;
		transform: translatex(calc(var(--percentage) * 100px));
	}

	.view div {
		background-position: center center;
		background-size: cover;
		position: absolute;
		width: 110%;
		height: 100%;
	}

	.view .bg1 {
		z-index: 10;
		opacity: calc(1 - (var(--percentage) - 0.5) / 0.5);
	}

	.view .bg2 {
		z-index: 20;
		opacity: calc(1 - (var(--percentage) - 0.25) / 0.25);
	}

	.view .bg3 {
		left: -10%;
	}

	header .view,
	header .bg1,
	header .bg2 {
		transition: .2s all ease-in;
	}

	header.moving .view,
	header.moving .bg1,
	header.moving .bg2 {
		transition: none;
	}

	.text-malfunction {
		position: absolute;
		top: 40%;
		left: 51.5%;
		transform: translate(-50%, -50%) scale(2.5);
		width: 220px;
		font-size: 34px;
		font-family: sans-serif;
		color: transparent;
	}

	.line {
		position: absolute;
		width: 200px;
		left: -1px;
		height: 1px;
		background: black;
		z-index: 50;
		animation: lineMove 5s ease-out infinite;
	}

	.text-malfunction:before, .text-malfunction:after {
		content: attr(data-word);
		position: absolute;
		top: 0;
		height: 36px;
		overflow: hidden;
		filter: contrast(200%);
	}

	.text-malfunction:before {
		left: 0;
		color: red;
		text-shadow: 1px 0 0 red;
		z-index: 30;
		animation: malfunctionAni 0.95s infinite;
	}

	.text-malfunction:after {
		left: -1px;
		color: cyan;
		text-shadow: -1px 0 0 cyan;
		z-index: 40;
		mix-blend-mode: lighten;
		animation: malfunctionAni 1.1s infinite 0.2s;
	}

	@keyframes lineMove {
		9% {
			top: 38px;
		}
		14% {
			top: 8px;
		}
		18% {
			top: 42px;
		}
		22% {
			top: 1px;
		}
		32% {
			top: 32px;
		}
		34% {
			top: 12px;
		}
		40% {
			top: 26px;
		}
		43% {
			top: 7px;
		}
		99% {
			top: 30px;
		}
	}

	@keyframes malfunctionAni {
		10% {
			top: -0.4px;
			left: -1.1px;
		}
		20% {
			top: 0.4px;
			left: -0.2px;
		}
		30% {
			left: .5px;
		}
		40% {
			top: -0.3px;
			left: -0.7px;
		}
		50% {
			left: 0.2px;
		}
		60% {
			top: 1.8px;
			left: -1.2px;
		}
		70% {
			top: -1px;
			left: 0.1px;
		}
		80% {
			top: -0.4px;
			left: -0.9px;
		}
		90% {
			left: 1.2px;
		}
		100% {
			left: -1.2px;
		}
	}

	.wrapper {
		position: absolute;
		width: 100px;
		bottom: 150px;
		left: 0;
		right: 0;
		margin: auto;
		font-size: 26px;
		z-index: 100;
	}

	.wrapper i {
		font-size: 60px;
		opacity: 0.5;
		cursor: pointer;
		position: absolute;
		top: 55px;
		left: 20px;
		animation: opener .5s ease-in-out alternate infinite;
		transition: opacity .2s ease-in-out, transform .5s ease-in-out .2s;
	}

	.wrapper i:hover {
		opacity: 1;
	}

	@keyframes opener {
		100% {
			top: 65px
		}
	}

	.wave1, .wave2 {
		position: absolute;
		bottom: 0;
		transition-duration: .4s, .4s;
		z-index: 80;
	}

	.wave1 {
		background: url('https://cdn.jsdelivr.net/gh/Naccl/blog-resource/img/wave1.png') repeat-x;
		height: 75px;
		width: 100%;
	}

	.wave2 {
		background: url('https://cdn.jsdelivr.net/gh/Naccl/blog-resource/img/wave2.png') repeat-x;
		height: 90px;
		width: calc(100% + 100px);
		left: -100px;
	}
</style>