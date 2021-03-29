/**
 * FlowTooltip 流程图节点悬浮提示内容
 * @author zhuyan.luo@hand-china.com
 * @date 2020/8/18
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import G6 from '@antv/g6';
import '@antv/g6/build/plugin.tool.tooltip';
import { isEmpty } from 'lodash';

// eslint-disable-next-line new-cap
const FlowTooltip = new G6.Plugins['tool.tooltip']({
  dx: 10,
  dy: 10,
  getTooltip({ item }) {
    if (item && item.type === 'node') {
      const model = item.getModel();
      const { taskHistoryList, label } = model;
      if (isEmpty(taskHistoryList)) {
        return '';
      }
      let lis = '';
      taskHistoryList.forEach((task) => {
        lis += `
        <tr  align="left" style="height: 28px; ">
          <td style="padding: 0 10px ">${task.assignee || ''}</td>
          <td style="padding: 0 10px ">${task.positionName || ''}</td>
          <td style="padding: 0 10px ">${task.unitName || ''}</td>
          <td style="padding: 0 10px ">${task.statusMeaning || ''}</td>
          <td style="padding: 0 10px; max-width: 205px; white-space: nowrap; word-break: keep-all; overflow: hidden; text-overflow: ellipsis; ">${
  task.commentContent || ''
}</td>
          <td style="padding: 0 10px ">${task.startDate || ''}</td>
          <td style="padding: 0 10px ">${task.endDate || ''}</td>
          </tr>
        `;
      });
      return `
              <div class="g6-tooltip" style="
                position: absolute;
                white-space: nowrap;
                zIndex: 8;
                box-shadow: 0px 0px 10px #aeaeae;
                line-height: 20px;
                transition: left 0.4s cubic-bezier(0.23, 1, 0.32, 1) 0s, top 0.4s cubic-bezier(0.23, 1, 0.32, 1) 0s;
                background-color:  rgb(255, 255, 255);
                border-width: 0px;
                border-color: rgb(51, 51, 51);
                border-radius: 4px;
                color: #333;
                font: 14px / 21px sans-serif;
                // padding: 5px;
                pointer-events: none;
              ">
                <div style="border-bottom: 1px solid #e8e8e8; padding: 5px;  font-size: 12px; ">审批记录【节点】：${label}】</div>
                <table cellSpacing="0" border="1" style="margin: 8px; color: #333; border-color: #e8e8e8; border: 1px solid #e8e8e8; font-size: 12px; font-family: Monospaced Number,Microsoft YaHei,Chinese Quote,-apple-s; font-weight: 500" >
                  <tr align="left" style="height: 32px;background-color: rgba(0,0,0,.04);" >
                    <td style="max-width: 150px;padding: 0 10px ">
                    审批人</td>
                    <td style="max-width: 150px;padding: 0 10px ">
                    岗位名称</td>
                    <td style="max-width: 150px;padding: 0 10px ">
                    部门名称</td>
                    <td style="max-width: 100px;padding: 0 10px ">
                    审批动作</td>
                    <td style="max-width: 150px;padding: 0 10px ">
                    审批意见</td>
                    <td style="max-width: 150px;padding: 0 10px ">
                    流程开始时间</td>
                    <td style="max-width: 150px;padding: 0 10px ">
                    审批时间</td>
                  </tr>
                  ${lis}
                </table>
              </div>
            `;
    }
  },
});

export { FlowTooltip };
