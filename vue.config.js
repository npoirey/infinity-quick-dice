const webpack = require('webpack')

module.exports = {
  lintOnSave: false,
  css: {
    loaderOptions: {
      sass: {
        data: `
          @import "~@/assets/styles.scss";
        `,
      },
    },
  },
  pwa: {
    workboxPluginMode: 'InjectManifest',
    workboxOptions: {
      swSrc: 'src/service-worker.js',
    },
  },
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        'process.env': {
          PACKAGE_JSON: '"' + escape(JSON.stringify(require('./package.json'))) + '"'
        }
      })
    ]
  },
};
