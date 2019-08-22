const path = require('path');
const fs = require('fs');

const pluginName = 'DumpVueEnvVarsWebpackPlugin';

/**
 * copied and adapted from https://stackoverflow.com/questions/54356415/how-can-i-customize-my-service-worker-based-on-environment-variables/57051150#57051150
 * set the version gotten from package.json and add it to a importable file for the service worker so that it can detect when it need to update
 */

module.exports = class DumpVersionWebpackPlugin {
  constructor(opts) {
    this.filename = opts.filename || 'version.js';
  }
  
  apply(compiler) {
    const fileContent = `const VUE_APP_VERSION = '${require('./package.json').version}'`;
    const outputDir = compiler.options.output.path;
    if (!fs.existsSync(outputDir)) {
      // ideally we'd let Webpack create it for us, but not sure how to
      // make this run later in the lifecycle
      fs.mkdirSync(outputDir);
    }
    const fullOutputPath = path.join(outputDir, this.filename);
    console.debug(
      `[DumpVersionWebpackPlugin] dumping version to file=${fullOutputPath}`,
    );
    fs.writeFileSync(fullOutputPath, fileContent);
  }
};