import { isUndefined } from 'lodash';

// import { isTenantRoleLevel, getCurrentOrganizationId } from 'hzero-front/lib/utils/utils';

const canvas = document.createElement('canvas');
const canvasContext = canvas.getContext('2d');
/**
 * optimizeMultilineText 格式化流程图节点标签文本的样式
 * @param {*} text 需要格式化的文本
 * @param {*} font 文本内容的当前字体属性
 * @param {*} maxRows 标签文本展示的最大行数
 * @param {*} maxWidth 标签文本的最大长度
 */
const optimizeMultilineText = (text, font, maxRows, maxWidth) => {
  canvasContext.font = font;
  if (canvasContext.measureText(text).width <= maxWidth) {
    return text;
  }
  let multilineText = [];
  let tempText = '';
  let tempTextWidth = 0;
  for (const char of text) {
    const { width } = canvasContext.measureText(char);
    if (tempTextWidth + width >= maxWidth) {
      multilineText.push(tempText);
      tempText = '';
      tempTextWidth = 0;
    }
    tempText += char;
    tempTextWidth += width;
  }

  if (tempText) {
    multilineText.push(tempText);
  }

  if (multilineText.length > maxRows) {
    const ellipsis = '...';
    const ellipsisWidth = canvasContext.measureText(ellipsis).width;

    let tempText2 = '';
    let tempTextWidth2 = 0;

    for (const char of multilineText[maxRows - 1]) {
      const { width } = canvasContext.measureText(char);

      if (tempTextWidth2 + width > maxWidth - ellipsisWidth) {
        break;
      }

      tempText2 += char;
      tempTextWidth2 += width;
    }

    multilineText = multilineText.slice(0, maxRows - 1).concat(`${tempText2}${ellipsis}`);
  }

  return multilineText.join('\n');
};

/**
 * 设置流程图节点的颜色-已审批的节点颜色为#5FFF5F，当前审批节点#0000FF，其他节点颜色为原色
 *
 * @export setColorForItems
 * @param {*} nodes - 节点列表
 * @param {*} historyList - 已审批节点+当前节点 列表
 * @param {*} language - 当前系统的语言
 * @returns
 */
const setColorForItems = (nodes, historyList, language) => {
  if (isUndefined(historyList)) {
    return nodes;
  }
  const temps = nodes.map((node) => {
    const historyItem = historyList.find((item) => item.nodeCode === node.id);
    if (historyItem) {
      return {
        ...node,
        color: historyItem.endDate ? '#5FFF5F' : '#0000FF',
        taskHistoryList: historyItem.taskHistoryList, // taskHistoryList - 审批记录，给鼠标悬浮节点时展示用
        label: node._tls?.name[language] || node.label,
      };
    }
    // return { ...node, color: '#A3B1BF' }; // 设置未执行的节点颜色为灰色
    return {
      ...node,
      label: node._tls?.name[language] || node.label,
      color: '#bfbfbf', // 设置未执行的节点颜色为灰色
    };
  });
  return temps;
};

/**
 * 设置流程图预测节点的展示数据，包括问题节点的颜色信息和节点Tooltip的信息
 *
 * @export setDataForItems
 * @param {*} nodes - 节点列表
 * @param {*} list - 流程启动后会流经的节点
 * @param {*} language - 当前系统的语言
 * @returns
 */
const setDataForItems = (nodes, list, language) => {
  if (isUndefined(list)) {
    return nodes;
  }
  const temps = nodes.map((node) => {
    const displayItem = list.find((item) => item.nodeCode === node.id);
    if (displayItem) {
      // 人工节点审批人列表和通知节点接收人列表为空就需要报错提示
      // const errorNode =
      //   ['manualNode', 'noticeNode'].includes(displayItem.nodeType) &&
      //   isEmpty(displayItem.forecastTasks);
      return {
        ...node,
        ...displayItem,
        color: displayItem.errorFlag === 1 ? '#CC0202' : '#5FFF5F',
        label: node._tls?.name[language] || node.label,
      };
    }
    // return { ...node, color: '#dbdbdb' }; // 设置未执行的节点颜色为灰色
    return {
      ...node,
      label: node._tls?.name[language] || node.label,
      color: '#bfbfbf', // 设置未执行的节点颜色为灰色
    };
  });
  return temps;
};

/**
 * 根据当前的角色层级获取接口是哪个层级
 */
// const getUrlPath = () => (isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : '');

export {
  // optimizeMultilineText, setColorForItems, setDataForItems, getUrlPath,
  optimizeMultilineText, setColorForItems, setDataForItems,
};
