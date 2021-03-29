/**
 * UserNode 人工节点
 * @author zhuyan.luo@hand-china.com
 * @date 2020/7/21
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { RegisterNode } from 'gg-editor2';

import { optimizeMultilineText } from '../../utils';

const UserNode = () => {
  const nodeConfig = {
    draw(item) {
      const keyShape = this.drawKeyShape(item);
      const group = item.getGraphicGroup();
      const { size, icon, label, errorFlag } = item.getModel();
      let [width, height] = size.split('*');
      width = parseInt(width, 10);
      height = parseInt(height, 10);
      const startX = -(width / 2);
      const startY = -(height / 2);
      // 绘制图标
      group.addShape('image', {
        attrs: {
          x: startX + 8,
          y: startY + 13,
          img: icon,
          width: 14,
          height: 14,
          cursor: 'pointer',
        },
      });
      // 绘制标签
      group.addShape('text', {
        attrs: {
          fontSize: 12,
          textAlign: 'left',
          fill: 'rgba(0,0,0,0.65)',
          x: startX + 14 + 12,
          y: startY + 14 + 8,
          type: 'text',
          text: optimizeMultilineText(label, 50, 2, 105 - 15 * 2),
          textBaseline: 'middle',
        },
      });

      if (errorFlag === 1) {
        // 状态 circle，只读流程图中需展示错误提醒
        group.addShape('circle', {
          attrs: {
            x: -startX - 16,
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
            x: -startX - 16,
            y: 7,
            text: '!',
            type: 'text',
          },
        });
      }
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

  return <RegisterNode name="manualNode" config={nodeConfig} extend="flow-rect" />;
};

export default UserNode;
