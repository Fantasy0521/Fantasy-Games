import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'
import Index from "@/views/Index";
import Home from "@/views/home/Home";
import Archives from "@/views/archives/Archives";
import Blog from "@/views/blog/Blog";
import Tag from "@/views/tag/Tag";
import Category from "@/views/category/Category";
import Moments from "@/views/moments/Moments";
import Friends from "@/views/friends/Friends";
import About from "@/views/about/About";
import Login from "@/views/Login";
import Game from "@/views/Game/game";
import GameDetail from "@/views/Game/GameDetail";
import Gpt from "@/views/gpt/Gpt.vue";

Vue.use(VueRouter)

const routes = [
	{
		path: '/login',
		component: Login,
		meta: {
			title: '登录'
		}
	},
	{
		path: '/gptHtml',
		name: 'gptHtml',
		component: Gpt,
		meta: {
			title: 'Fantasy Gpt'
		}
	},
	{
		path: '/',
		component: Index,
		redirect: '/home',
		children: [
			{
				path: '/home',
				name: 'home',
				component: Home,
				meta: {
					title: '首页'
				}
			},
			{
				path: '/archives',
				name: 'archives',
				component: Archives,
				meta: {
					title: '归档'
				}
			},
			{
				path: '/blog/:id',
				name: 'blog',
				component: Blog,
				meta: {
					title: '帖子'
				}
			},
			{
				path: '/gameDetail/:id',
				name: 'gameDetail',
				component: GameDetail,
				meta: {
					title: '游戏'
				}
			},
			{
				path: '/tag/:name',
				name: 'tag',
				component: Tag,
				meta: {
					title: '标签'
				}
			},
			{
				path: '/category/:name',
				name: 'category',
				component: Category,
				meta: {
					title: '分类'
				}
			},
			{
				path: '/game/:games',
				name: 'game',
				component: Game,
				meta: {
					title: '游戏库'
				}
			},
			{
				path: '/moments',
				name: 'moments',
				component: Moments,
				meta: {
					title: '动态'
				}
			},
			{
				path: '/gpt',
				name: 'gpt',
				component: Gpt,
				meta: {
					title: 'Fantasy Gpt'
				}
			},
			{
				path: '/friends',
				name: 'friends',
				component: Friends,
				meta: {
					title: '友人帐'
				}
			},
			{
				path: '/about',
				name: 'about',
				component: About,
				meta: {
					title: '关于我'
				}
			}
		]
	}
]

const router = new VueRouter({
	mode: 'history',
	base: process.env.BASE_URL,
	routes
})

//挂载路由守卫
router.beforeEach((to, from, next) => {
	if (to.meta.title) {
		if (store.state.siteInfo.webTitleSuffix) {
			document.title = to.meta.title + store.state.siteInfo.webTitleSuffix
		} else {
			document.title = to.meta.title
		}
	}
	next()
})

export default router
