import React, { useMemo, forwardRef } from 'react';
import { Select, Tooltip } from 'choerodon-ui/pro';
import useSelect, { SelectConfig } from '@choerodon/agile/lib/hooks/useSelect';
import { approveApi } from '@/api';
import { SelectProps } from 'choerodon-ui/pro/lib/select/Select';
import FlatSelect from '@choerodon/agile/lib/components/flat-select';

export interface IEmployee {
  realName: string
  employeeNum: string
  positionName?: string
  unitName?: string,
  unitCompanyName?: string
}

interface Props extends Partial<SelectProps> {
  flat?: boolean,
  request?: () => Promise<any>,
  selfEmpNum: string
  afterLoad?: (employees: IEmployee[]) => void,
  dataRef?: React.MutableRefObject<any>
}

const SelectEmployee: React.FC<Props> = forwardRef(({
  request, flat, selfEmpNum, afterLoad, dataRef, ...otherProps
}, ref: React.Ref<Select>) => {
  const config = useMemo((): SelectConfig => ({
    name: 'employee',
    textField: 'realName',
    valueField: 'id',
    request: ({ page, filter }) => {
      if (!request) {
        return approveApi.getEmployees(page, selfEmpNum, filter);
      }
      return request();
    },
    paging: true,
    middleWare: (data) => {
      if (dataRef) {
        Object.assign(dataRef, {
          current: data,
        });
      }
      if (afterLoad) {
        // @ts-ignore
        afterLoad(data);
      }
      return data;
    },
    optionRenderer: (item: IEmployee) => (
      <>
        {
          item.unitName ? (
            <Tooltip title={`部门：${item.unitName}${item.positionName ? `，岗位：${item.positionName}` : ''}`}>
              <span>{`${item.realName}`}</span>
            </Tooltip>
          ) : (
            <span>{`${item.realName}`}</span>
          )
        }
      </>
    ),
  }), [request, selfEmpNum]);
  const props = useSelect(config);
  const Component = flat ? FlatSelect : Select;
  return (
    <Component
      ref={ref}
      {...props}
      {...otherProps}
    />
  );
});
export default SelectEmployee;
