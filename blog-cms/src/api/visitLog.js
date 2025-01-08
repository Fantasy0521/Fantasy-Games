import axios from '@/util/request'

export function getVisitLogList(queryInfo) {
	return axios({
		url: 'fantasy/visitLog/visitLogs',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function deleteVisitLogById(id) {
	return axios({
		url: 'fantasy/visitLog/visitLog',
		method: 'DELETE',
		params: {
			id
		}
	})
}