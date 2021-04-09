import { DataSet } from 'choerodon-ui/pro';
import { find } from 'lodash';
import { Choerodon } from '@choerodon/master';
import React, {
  useCallback, useMemo, forwardRef, useImperativeHandle,
} from 'react';
import { usePersistFn } from 'ahooks';
import { ICommentTemplate } from '@/common/types';
import { ProcessAttachmentProps } from './components/process-attachment';
import { CommentTemplateManageProps } from './components/comment-template-manage';
import DefaultLayout from './components/layouts/DefaultLayout';
import DetailLayout from './components/layouts/DetailLayout';

export type SuggestProps = {
  /* 两种布局 */
  layout?: 'default' | 'detail'
  data: ICommentTemplate[]
  onSaveCommentTemplate: (commentContent: string) => void
} & ProcessAttachmentProps & CommentTemplateManageProps
export interface SuggestRef {
  dataSet: DataSet
}
const Suggest: React.ForwardRefRenderFunction<SuggestRef, SuggestProps> = (props, ref) => {
  const {
    layout = 'default',
    data,
    manageVisible,
    setManageVisible,
    onSaveCommentTemplate,
  } = props;
  const commentTemplates = data;
  const handleUpdate = usePersistFn(({ record, name, value }) => {
    if (name === 'commentTemplates') {
      record.set('commentContent', find(commentTemplates, { commentTemplateId: value })?.commentContent);
    }
  });

  const dataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [{
      name: 'commentTemplates',
      // required: true,
      label: '审批意见',
    }, {
      name: 'commentContent',
      required: true,
      label: '点击添加审批意见',
    }],
    events: {
      update: handleUpdate,
    },
  }), [handleUpdate]);
  useImperativeHandle(ref, () => ({ dataSet }));
  const handleSaveCommentTemplate = usePersistFn(async () => {
    if (await dataSet.current?.getField('commentContent')?.checkValidity()) {
      const commentContent = dataSet.current?.get('commentContent');
      if (commentContent) {
        await onSaveCommentTemplate(commentContent);
        Choerodon.prompt('保存成功', 'success');
      }
    }
  });
  const handleManageClick = useCallback(() => {
    setManageVisible(!manageVisible);
  }, [manageVisible, setManageVisible]);
  if (layout === 'detail') {
    return (
      <DetailLayout
        {...props}
        onManageClick={handleManageClick}
        onSaveCommentTemplateClick={handleSaveCommentTemplate}
        dataSet={dataSet}
      />
    );
  }
  return (
    <DefaultLayout
      {...props}
      onManageClick={handleManageClick}
      onSaveCommentTemplateClick={handleSaveCommentTemplate}
      dataSet={dataSet}
    />
  );
};

export default forwardRef(Suggest);
