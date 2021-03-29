import React from 'react';
import { Tooltip } from 'choerodon-ui/pro';
import { Collapse } from 'choerodon-ui';
import { Input, Tag } from 'hzero-ui'; // c7n-ui点放大镜竟然不会触发onSearch，所以放弃了
import { Item } from 'gg-editor2';
import uuid from 'uuid/v4';
// import request from 'hzero-front/lib/utils/request';
// import { getResponse } from 'hzero-front/lib/utils/utils';

import startNodeIcon from './assets/icons/start@2x.png';
import endNodeIcon from './assets/icons/end@2x.png';
import userNodeIcon from './assets/icons/artificial@2x.png';
import noticeNodeIcon from './assets/icons/notice@2x.png';
import serviceNodeIcon from './assets/icons/service@2x.png';
import exclusiveNodeIcon from './assets/icons/exclusive@2x.png';
import parallelNodeIcon from './assets/icons/parallel@2x.png';
// import { getUrlPath } from '../utils';

import styles from './panel.less';

const { Panel } = Collapse;
const { Search } = Input;

// const urlPath = getUrlPath();

/**
 * 渲染流程图节点
 * @param item
 * @param props
 */
const renderNode = (item, props) => <Panel key={uuid()} header={renderNodeItem(item, props)} />;

/**
 * 渲染g6节点
 * @param item
 * @param props
 * @returns {*}
 */
const renderNodeItem = (item) => {
  const {
    _key, _name, _icon, _nodeId, color, _size, _tls,
  } = item;
  const nodeModel = {
    name,
    label: _name,
    type: _key,
    icon: _icon,
    _size: `${_name.length * 12 + 35}*32`,
    _nodeId,
    color,
    _tls,
  };
  return (
    <div className={styles['item-node-name']}>
      <Item
        type="node"
        model={nodeModel}
        shape={_key}
        size={_size || '120*42'} // 除 人工节点、通知节点和服务节点的长宽固定成120*42外，其他节点的长宽在节点自定义_size
      >
        {renderNodeName(item)}
      </Item>
    </div>
  );
};

/**
 * 渲染提示框
 * @param item
 * @returns {*}
 */
const renderNodeName = (item) => {
  const { _name, _icon } = item;
  return (
    <Tooltip title={_name} placement="topLeft" mouseEnterDelay={1}>
      <img alt={_name} src={_icon} />
      {_name}
    </Tooltip>
  );
};

const useSearch = () => {
  const [searchValues, setSearchValues] = React.useState([]);
  const [currentSearchValue, setCurrentSearchValue] = React.useState();
  const onSearchValueChange = (value) => {
    setSearchValues((pre) => [value, ...pre.filter((o) => o !== value)]);
  };

  const filterChildren = (item) => {
    const currentValue = searchValues[0];
    if (!currentValue) {
      return true;
    }
    return item._name.indexOf(currentValue) !== -1;
  };

  const renderSearchValues = () => searchValues.filter(Boolean).map((value) => (
    <Tag
      key={value}
      style={{ marginTop: '5px' }}
      closable
      onClose={() => handleCloseTag(value)}
      onClick={(e) => handleClickTag(e, value)}
    >
      {value}
    </Tag>
  ));

  const handleCloseTag = (value) => {
    if (value === currentSearchValue) {
      setCurrentSearchValue();
      setSearchValues((pre) => ['', ...pre]);
    }
    setSearchValues((pre) => pre.filter((o) => o !== value));
  };

  const handleClickTag = (e, value) => {
    if (e.target.tagName === 'DIV') {
      setCurrentSearchValue(value);
      onSearchValueChange(value);
    }
  };

  const handleValueChange = (e) => {
    e.persist();
    const { value } = e.target;
    setCurrentSearchValue(value);
  };

  return {
    currentSearchValue,
    handleValueChange,
    onSearchValueChange,
    renderSearchValues,
    filterChildren,
  };
};

const EditorItemPanel = (props) => {
  const typeId = React.useMemo(() => props.match.params.typeId, [props.match.params.typeId]);
  const [noticeItemList, setNoticeItemList] = React.useState([]);
  const [serviceItemList, setServiceItemList] = React.useState([]);
  const {
    onSearchValueChange,
    filterChildren,
    renderSearchValues,
    currentSearchValue,
    handleValueChange,
  } = useSearch();

  // const queryNoticeItem = React.useCallback(async () => {
  //   const res = await request(`/hwkf/v1/${urlPath}notice-nodes`, {
  //     method: 'GET',
  //     query: { page: -1, typeId },
  //   });
  //   let noticeItems = [];
  //   if (getResponse(res)) {
  //     noticeItems = (res.content || []).map((item) => ({
  //       _key: 'noticeNode',
  //       _type: 'NOTICE_TASK',
  //       _name: item.nodeName,
  //       _icon: noticeNodeIcon,
  //       _nodeId: item.nodeId,
  //       color: '#FA8C16',
  //       _tls: { name: item._tls?.nodeName },
  //     }));
  //   }
  //   return noticeItems;
  // }, []);
  // const queryServiceItem = React.useCallback(async () => {
  //   const res = await request(`/hwkf/v1/${urlPath}def-events`, {
  //     method: 'GET',
  //     query: {
  //       page: -1, typeId, eventType: 'SERVICE', enabledFlag: 1,
  //     },
  //   });
  //   let serviceItems = [];
  //   if (getResponse(res)) {
  //     serviceItems = (res.content || []).map((item) => ({
  //       _key: 'eventNode',
  //       _type: 'SERVICE_TASK',
  //       _name: item.eventName,
  //       _icon: serviceNodeIcon,
  //       _nodeId: item.eventId,
  //       color: '#1890FF',
  //       _tls: { name: item._tls?.eventName },
  //     }));
  //   }
  //   return serviceItems;
  // }, []);
  React.useEffect(() => {
    // queryNoticeItem().then(setNoticeItemList);
    // queryServiceItem().then(setServiceItemList);
  }, []);
  const LIST_DATA = [
    {
      _key: 'commons',
      _name: '节点',
      _children: [
        {
          _key: 'startNode',
          _type: 'START_NODE',
          _name: '开始',
          _icon: startNodeIcon,
          _size: '30*30',
          color: '#59CFAA',
        },
        {
          _key: 'endNode',
          _type: 'END_NODE',
          _name: '结束',
          _icon: endNodeIcon,
          _size: '30*30',
          color: '#FF4C4C',
        },
        {
          _key: 'manualNode',
          _type: 'USER_TASK',
          _name: '人工节点',
          _icon: userNodeIcon,
          color: '#13C2C2',
        },
      ],
    },
    {
      _key: 'notices',
      _name: '通知',
      _children: [...noticeItemList],
    },
    {
      _key: 'services',
      _name: '服务',
      _children: [...serviceItemList],
    },
    {
      _key: 'gateways',
      _name: '网关',
      _children: [
        {
          _key: 'exclusiveGatewayExecutorNode',
          _type: 'EXCLUSIVE_GATEWAY',
          _name: '排他网关',
          _icon: exclusiveNodeIcon,
          color: '#CC3399',
          _size: '60*42',
        },
        {
          _key: 'exclusiveGatewayJoin', // TODO:
          _type: 'EXCLUSIVE_GATEWAY_JOIN',
          _name: '排他收敛网关',
          _icon: exclusiveNodeIcon,
          color: '#ae6642',
          _size: '60*42',
        },
        {
          _key: 'parallelGatewayNode',
          _type: 'PARALLEL_GATEWAY',
          _name: '并行网关',
          _icon: parallelNodeIcon,
          color: '#722ED1',
          _size: '60*42',
        },
      ],
    },
  ];

  const renderPanel = () => {
    const panels = LIST_DATA.map((item) => (
      <Panel header={item._name} key={item._key} className={styles['hwkf-left-panel']}>
        {item._children.filter(filterChildren).map((subNode) => renderNode(subNode, props))}
      </Panel>
    ));
    return panels;
  };
  return (
    <>
      <div style={{ padding: '5px' }}>
        <Search
          onSearch={onSearchValueChange}
          onChange={handleValueChange}
          value={currentSearchValue}
        />
        <div style={{ marginTop: '5px' }}>
          搜索历史
        </div>
        {renderSearchValues()}
      </div>
      <Collapse bordered={false} defaultActiveKey={['commons', 'notices', 'services', 'gateways']}>
        {renderPanel()}
      </Collapse>
    </>
  );
};

export default EditorItemPanel;
