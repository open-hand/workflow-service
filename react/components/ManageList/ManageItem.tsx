import React, {
  useState, useCallback, useRef, MutableRefObject,
} from 'react';
import {
  Icon, Tooltip, TextField,
} from 'choerodon-ui/pro';
import ObserverTextField from 'choerodon-ui/pro/lib/text-field/TextField';
import { Popconfirm } from 'choerodon-ui';
import { ManageListProps } from './ManageList';

interface Props {
  data: Record<string, any>
  onSubmit: ManageListProps['onSubmit']
  onDelete: ManageListProps['onDelete']
  validator?: ManageListProps['validator']
  textField: ManageListProps['textField']
}
const FilterItem: React.FC<Props> = ({
  data, textField, onSubmit, onDelete, validator,
}) => {
  const name = data[textField];
  const [isEditing, setIsEditing] = useState(false);
  const valueRef = useRef<string>(name);
  const inputRef = useRef() as MutableRefObject<ObserverTextField>;
  const handleCancel = useCallback(() => {
    setIsEditing(false);
  }, []);
  const handleSubmit = useCallback(() => {
    const newValue = valueRef.current;
    onSubmit && onSubmit(data, newValue);
  }, [data, onSubmit]);
  const handleDelete = useCallback(() => {
    onDelete && onDelete(data);
  }, [data, onDelete]);
  return (
    <li className="c7n-ManageList-item">
      {
        isEditing ? (
          <TextField
            ref={inputRef}
            required
            validator={validator}
            autoFocus
            defaultValue={name}
            onChange={(newValue) => {
              valueRef.current = newValue;
            }}
            onInput={(e) => {
              // @ts-ignore
              inputRef.current.validate(e.target.value);
            }}
            onBlur={() => {
              setTimeout(handleCancel, 200);
            }}
            maxLength={10}
          />
        ) : (<span>{name}</span>)
      }
      <span className="c7n-filterAction">
        {isEditing ? (
          <>
            <Tooltip title="保存">
              <Icon
                type="check"
                className="c7n-filterAction-icon"
                onClick={handleSubmit}
              />
            </Tooltip>
            <Tooltip title="取消">
              <Icon
                type="close"
                className="c7n-filterAction-icon"
                onClick={handleCancel}
              />
            </Tooltip>
          </>
        ) : (
          <>
            <Tooltip title="修改">
              <Icon
                type="mode_edit"
                className="c7n-filterAction-icon"
                onClick={() => {
                  setIsEditing(true);
                }}
              />
            </Tooltip>
            <Popconfirm title="确认删除" placement="bottomRight" onConfirm={handleDelete}>
              <Icon
                type="delete_forever"
              />
            </Popconfirm>
          </>
        )}

      </span>
    </li>
  );
};
export default FilterItem;
