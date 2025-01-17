import axios from '@/util/request'

export function getDataByQuery(queryInfo) {
	return axios({
		url: 'fantasy/calculate/calculates',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function deleteCalculateById(id) {
	return axios({
		url: 'fantasy/calculate',
		method: 'DELETE',
		params: {
			id
		}
	})
}


export function saveCalculate(calculate) {
	return axios({
		url: 'fantasy/calculate',
		method: 'POST',
		data: {
			...calculate
		}
	})
}

export function updateTop(id, top) {
	return axios({
		url: 'fantasy/calculate/top',
		method: 'PUT',
		params: {
			id,
			top
		}
	})
}

export function getCalculateById(id) {
	return axios({
		url: 'fantasy/calculate',
		method: 'GET',
		params: {
			id
		}
	})
}

export function updateCalculate(calculate) {
	return axios({
		url: 'fantasy/calculate',
		method: 'PUT',
		data: {
			...calculate
		}
	})
}