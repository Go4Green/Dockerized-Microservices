module.exports = {
  mode:'development',
  devtool: 'inline-source-map',
  resolve: {
    // extensions: ['.ts', '.tsx', '.js', '.webpack.js']
    extensions: ['.js', '.webpack.js']
  },
  // entry: './src/index.ts',
  entry: './src/app.js',
  output: {
    path: __dirname + '/dist',
    filename: 'app.js'
  },
  module: {
    rules: [
      // { test: /\.tsx?$/, loader: "ts-loader" }
    ],
    // loaders: [
    //   {test: /\.tsx?$/, loader: 'ts-loader', exclude: /node_modules/}
    // ]
  },
  watch: true,
  devServer: {}
};
