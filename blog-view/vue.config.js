module.exports = {
	devServer: {
		port: 8280,
		// open: true,
		// overlay: {
		// 	warnings: false,
		// 	errors: true
		// },
	},
	configureWebpack: {
		resolve: {
			alias: {
				'assets': '@/assets',
				'common': '@/common',
				'components': '@/components',
				'api': '@/api',
				'views': '@/views',
				'plugins': '@/plugins'
			}
		}
	}
}