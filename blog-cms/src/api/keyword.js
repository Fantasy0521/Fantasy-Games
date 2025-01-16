import axios from '@/util/request'

export function getDataByQuery(queryInfo) {
	return axios({
		url: 'fantasy/keyword/keywords',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function deleteKeywordById(id) {
	return axios({
		url: 'fantasy/keyword',
		method: 'DELETE',
		params: {
			id
		}
	})
}


export function saveKeyword(keyword) {
	return axios({
		url: 'fantasy/keyword',
		method: 'POST',
		data: {
			...keyword
		}
	})
}

export function updateTop(id, top) {
	return axios({
		url: 'fantasy/keyword/top',
		method: 'PUT',
		params: {
			id,
			top
		}
	})
}

export function getKeywordById(id) {
	return axios({
		url: 'fantasy/keyword',
		method: 'GET',
		params: {
			id
		}
	})
}

export function updateKeyword(keyword) {
	return axios({
		url: 'fantasy/keyword',
		method: 'PUT',
		data: {
			...keyword
		}
	})
}