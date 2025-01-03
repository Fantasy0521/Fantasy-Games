import axios from '@/plugins/axios'

export function getGameListByCategoryName(categoryName,keyword, pageNum) {
	return axios({
		url: 'fantasy/game/category',
		method: 'GET',
		params: {
			categoryName,
			keyword,
			pageNum
		}
	})
}