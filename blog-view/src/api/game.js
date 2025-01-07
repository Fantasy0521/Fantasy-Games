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

export function getGameById(token, id) {
	return axios({
		url: 'fantasy/game',
		method: 'GET',
		headers: {
			Authorization: token,
		},
		params: {
			id
		}
	})
}