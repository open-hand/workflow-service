/**
 * StartNode 开始节点
 * @author zhuyan.luo@hand-china.com
 * @date 2020/7/21
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { RegisterNode } from 'gg-editor2';

class StartNode extends React.Component {
  render() {
    const nodeConfig = {
      draw(item) {
        const keyShape = this.drawKeyShape(item);
        const group = item.getGraphicGroup();
        const { color } = item.getModel();
        group.addShape('circle', {
          attrs: {
            x: 0,
            y: 0,
            r: 15,
            type: 'circle',
            stroke: color,
            lineWidth: 2,
          },
        });
        return keyShape;
      },
      afterDraw() {},
      anchor: [
        [0.5, 0],
        [0.5, 1],
        [0, 0.5],
        [1, 0.5],
      ],
    };

    return <RegisterNode name="startNode" config={nodeConfig} extend="flow-circle" />;
  }
}

export default StartNode;
