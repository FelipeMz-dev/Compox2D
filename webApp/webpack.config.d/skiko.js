const path = require('path');

config.devServer = config.devServer || {};
const extraStaticDir = path.resolve(__dirname, 'kotlin');

if (!config.devServer.static) {
    config.devServer.static = [extraStaticDir];
} else if (Array.isArray(config.devServer.static)) {
    config.devServer.static.push(extraStaticDir);
} else {
    config.devServer.static = [config.devServer.static, extraStaticDir];
}

config.module = config.module || {};
config.module.rules = config.module.rules || [];

// Ensure skiko.wasm keeps its exact filename instead of being renamed to a hash by Webpack
config.module.rules.push({
    test: /\.wasm$/,
    type: 'asset/resource',
    generator: {
        filename: '[name][ext]'
    }
});

