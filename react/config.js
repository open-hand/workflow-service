const config = {
  local: true,
  // use for c7n start
  clientId: 'localhost', // 必须填入响应的客户端（本地开发）
  // port: 8080,
  master: './node_modules/@choerodon/master/lib/master.js',
  projectType: 'choerodon',
  buildType: 'single',
  modules: [
    '.',
  ],
  dashboard: {},
  resourcesLevel: ['site', 'origanization', 'project', 'user'],
  webSocketServer: 'ws://notify.staging.saas.hand-china.com',
};
module.exports = config;
