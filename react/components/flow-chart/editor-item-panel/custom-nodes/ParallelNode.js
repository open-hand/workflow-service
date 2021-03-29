/**
 * ParallelNode 并行网关
 * @author zhuyan.luo@hand-china.com
 * @date 2020/7/21
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { RegisterNode } from 'gg-editor2';

// import { optimizeMultilineText } from '../../utils';

class ParallelNode extends React.Component {
  render() {
    const config = {
      draw(item) {
        const keyShape = this.drawKeyShape(item);
        const group = item.getGraphicGroup();
        const { color, errorFlag } = item.getModel();
        // 绘制标签
        group.addShape('text', {
          attrs: {
            fontSize: 37,
            fontWeight: 600,
            textAlign: 'center',
            fill: color,
            x: 0,
            y: 1,
            text: '＋',
            type: 'text',
            textBaseline: 'middle',
          },
        });

        if (errorFlag === 1) {
          // 状态 circle，只读流程图中需展示错误提醒
          group.addShape('circle', {
            attrs: {
              x: 16,
              y: 0,
              r: 6,
              fill: '#CC0202',
              stroke: '#CC0202',
            },
          });
          group.addShape('text', {
            attrs: {
              fontSize: 12,
              textAlign: 'center',
              fill: '#fff',
              x: 16,
              y: 7,
              text: '!',
              type: 'text',
            },
          });
        }
        return keyShape;
      },
      anchor: [
        [0.5, 0],
        [0.5, 1],
        [0, 0.5],
        [1, 0.5],
      ],
    };

    return <RegisterNode name="parallelGatewayNode" config={config} extend="flow-rhombus" />;
  }
}

export default ParallelNode;
