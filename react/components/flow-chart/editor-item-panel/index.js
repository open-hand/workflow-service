/**
 * FlowItemPanel 流程编辑器属性面板
 * @author zhuyan.luo@hand-china.com
 * @date 2020/8/6
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { ItemPanel } from 'gg-editor2';
import {
  StartNode,
  EndNode,
  UserNode,
  NoticeNode,
  ServiceNode,
  ExclusiveNode,
  MergeNode,
  ParallelNode,
} from './custom-nodes';
import EditorItemPanel from './EditorItemPanel';
import styles from './index.less';

const FlowItemPanel = (props) => (
  <div className={styles['hwkf-item-panel']}>
    <div className={styles['panel-title']}>
      组件
    </div>
    <ItemPanel className={styles['panel-content']}>
      <StartNode />
      <EndNode />
      <UserNode />
      <NoticeNode />
      <ServiceNode />
      <ExclusiveNode />
      <MergeNode />
      <ParallelNode />
      <EditorItemPanel {...props} />
    </ItemPanel>
  </div>
);

export default FlowItemPanel;
