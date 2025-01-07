import axios from '@/util/request'

export function getData(queryInfo) {
	return axios({
		url: 'fantasy/category/categories',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function addCategory(form) {
	return axios({
		url: 'fantasy/category',
		method: 'POST',
		data: {
			...form
		}
	})
}

export function editCategory(form) {
	return axios({
		url: 'fantasy/category',
		method: 'PUT',
		data: {
			...form
		}
	})
}

export function deleteCategoryById(id) {
	return axios({
		url: 'fantasy/category',
		method: 'DELETE',
		params: {
			id
		}
	})
}