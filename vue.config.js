const webpack = require('webpack');
const DumpVersionWebpackPlugin = require('./DumpVersionWebpackPlugin.js')

process.env.VUE_APP_VERSION = require('./package.json').version;

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
      // define env vars to use to show version in app
      /*new webpack.DefinePlugin({
        'IQD_VERSION': JSON.stringify(packageJson.version),
      }),*/
      // dump the env vars in a file importable by our service worker (again to get version from package.json)
      new DumpVersionWebpackPlugin({ filename: 'version.js' })
    ]
  },
};
