declare module '@choerodon/boot';
declare module '@choerodon/master*';
declare module '@choerodon/agile*';
declare module 'query-string';
declare module 'timeago-react';
declare module 'file-saver';
declare module '*.svg' {
  import * as React from 'react';

  export const ReactComponent: React.FC<React.SVGProps<SVGSVGElement>>;

  const src: string;
  export default src;
}
interface Window {
  _env_: { [key: string]: string }
}

declare module 'hzero-front-hwkf/lib/components/flow-chart';
